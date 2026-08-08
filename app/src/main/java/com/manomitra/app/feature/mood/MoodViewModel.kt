package com.manomitra.app.feature.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manomitra.app.feature.chat.AIRepository
import com.manomitra.app.feature.chat.AIRepositoryProvider
import com.manomitra.app.feature.journal.JournalRepository
import com.manomitra.app.feature.journal.FirestoreJournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MoodViewModel(
    private val moodRepository: MoodRepository = FirestoreMoodRepository(),
    private val dailyInsightRepository: DailyInsightRepository = FirestoreDailyInsightRepository(),
    private val journalRepository: JournalRepository = FirestoreJournalRepository(),
    private val aiRepository: AIRepository = AIRepositoryProvider.get(),
    private val fallbackAiRepository: AIRepository = com.manomitra.app.feature.chat.GeminiRepository(),
    private val getCurrentUserId: () -> String? = { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
) : ViewModel() {

    private val _currentMood = MutableStateFlow<MoodEntry?>(null)
    val currentMood: StateFlow<MoodEntry?> = _currentMood.asStateFlow()

    private val _recentMoods = MutableStateFlow<List<MoodEntry>>(emptyList())
    val recentMoods: StateFlow<List<MoodEntry>> = _recentMoods.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _aiAnalysis = MutableStateFlow<String?>(null)
    val aiAnalysis: StateFlow<String?> = _aiAnalysis.asStateFlow()

    // Daily Insight States
    private val _dailyInsight = MutableStateFlow<DailyInsight?>(null)
    val dailyInsight: StateFlow<DailyInsight?> = _dailyInsight.asStateFlow()

    private val _isInsightLoading = MutableStateFlow(false)
    val isInsightLoading: StateFlow<Boolean> = _isInsightLoading.asStateFlow()

    private val _insightError = MutableStateFlow<String?>(null)
    val insightError: StateFlow<String?> = _insightError.asStateFlow()

    init {
        loadMoodHistory()
    }

    fun loadMoodHistory() {
        val userId = getCurrentUserId()
        if (userId == null) {
            _error.value = "User not authenticated"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = moodRepository.getMoodHistory(userId)
            result.onSuccess { list ->
                _recentMoods.value = list
                
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val todayStr = sdf.format(Date())
                val todayMood = list.firstOrNull { sdf.format(Date(it.createdAt)) == todayStr && it.source == MoodSource.MANUAL.name }
                _currentMood.value = todayMood
                
                _streak.value = calculateStreak(list)
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load mood history"
            }
            _isLoading.value = false
        }
    }

    fun loadDailyInsight(language: String = "English") {
        if (_dailyInsight.value != null || _isInsightLoading.value) return
        val userId = getCurrentUserId() ?: return

        viewModelScope.launch {
            _isInsightLoading.value = true
            _insightError.value = null
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

            // 1. Check Firestore Cache
            val cacheResult = dailyInsightRepository.getDailyInsight(userId, todayStr)
            cacheResult.onSuccess { cached ->
                if (cached != null) {
                    _dailyInsight.value = cached
                    _isInsightLoading.value = false
                    return@launch
                }
            }

            // 2. Fetch recent journal summaries
            val journalSummaries = mutableListOf<String>()
            try {
                val flowResult = journalRepository.getJournalHistory(userId).firstOrNull()
                flowResult?.onSuccess { list ->
                    val summaries = list.mapNotNull { it.aiAnalysis?.get("shortSummary") as? String }
                        .filter { it.isNotEmpty() }
                    journalSummaries.addAll(summaries)
                }
            } catch (e: Exception) {
                // Non-fatal
            }

            // 3. Request Daily Insight
            val moods = _recentMoods.value
            var aiResult = aiRepository.generateDailyInsight(moods, journalSummaries, language)
            var activeProvider = "Groq"

            if (aiResult.isFailure) {
                val fallbackResult = fallbackAiRepository.generateDailyInsight(moods, journalSummaries, language)
                if (fallbackResult.isSuccess) {
                    aiResult = fallbackResult
                    activeProvider = "Gemini"
                }
            }
            
            aiResult.onSuccess { text ->
                val newInsight = DailyInsight(
                    date = todayStr,
                    insight = text,
                    moodSummary = if (moods.isNotEmpty()) moods.first().mood else "NEUTRAL",
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis(),
                    provider = activeProvider
                )
                
                val saveResult = dailyInsightRepository.saveDailyInsight(userId, newInsight)
                saveResult.onSuccess {
                    _dailyInsight.value = newInsight
                }.onFailure {
                    _dailyInsight.value = newInsight
                }
            }.onFailure { exception ->
                _insightError.value = exception.message ?: "Failed to generate wellness insight"
            }
            _isInsightLoading.value = false
        }
    }

    fun saveMood(moodName: String, note: String = "") {
        val userId = getCurrentUserId()
        if (userId == null) {
            _error.value = "User not authenticated"
            return
        }

        val type = MoodEntry.fromString(moodName)
        val score = MoodEntry.mapToScore(type)
        
        val entry = MoodEntry(
            mood = type.name,
            moodScore = score,
            note = note,
            source = MoodSource.MANUAL.name
        )

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = moodRepository.saveMood(userId, entry)
            result.onSuccess {
                loadMoodHistory()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to save mood"
            }
            _isLoading.value = false
        }
    }

    fun requestMoodAnalysis() {
        val moods = _recentMoods.value
        if (moods.isEmpty()) {
            _error.value = "No mood history available for analysis"
            return
        }

        val recentForAi = moods.take(14)

        viewModelScope.launch {
            _isAnalyzing.value = true
            _error.value = null
            _aiAnalysis.value = null
            val result = aiRepository.analyzeMood(recentForAi)
            result.onSuccess { jsonResponse ->
                _aiAnalysis.value = jsonResponse
            }.onFailure { exception ->
                _error.value = exception.message ?: "AI Analysis failed"
            }
            _isAnalyzing.value = false
        }
    }

    fun deleteMood(moodId: String) {
        val userId = getCurrentUserId()
        if (userId == null) {
            _error.value = "User not authenticated"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = moodRepository.deleteMood(userId, moodId)
            result.onSuccess {
                loadMoodHistory()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to delete mood"
            }
            _isLoading.value = false
        }
    }

    fun setSelectedDate(timestamp: Long) {
        _selectedDate.value = timestamp
    }

    fun calculateStreak(moods: List<MoodEntry>): Int {
        if (moods.isEmpty()) return 0
        
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val uniqueDates = moods.map { sdf.format(Date(it.createdAt)) }.toSet()
        
        val calendar = Calendar.getInstance()
        var streak = 0
        
        var dateToCheck = calendar.time
        var dateStr = sdf.format(dateToCheck)
        
        if (dateStr !in uniqueDates) {
            calendar.add(Calendar.DATE, -1)
            dateToCheck = calendar.time
            dateStr = sdf.format(dateToCheck)
            if (dateStr !in uniqueDates) {
                return 0
            }
        }
        
        while (dateStr in uniqueDates) {
            streak++
            calendar.add(Calendar.DATE, -1)
            dateToCheck = calendar.time
            dateStr = sdf.format(dateToCheck)
        }
        
        return streak
    }
}
