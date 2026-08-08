package com.manomitra.app.feature.journal

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manomitra.app.feature.chat.AIRepository
import com.manomitra.app.feature.chat.AIRepositoryProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class JournalViewModel(
    private val journalRepository: JournalRepository = FirestoreJournalRepository(),
    private val aiRepository: AIRepository = AIRepositoryProvider.get(),
    private val getCurrentUserId: () -> String? = { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val journalHistory = MutableStateFlow<List<JournalEntry>>(emptyList())
    val todayEntry = MutableStateFlow<JournalEntry?>(null)

    init {
        loadJournalHistory()
    }

    fun clearError() {
        errorMessage = null
    }

    fun loadJournalHistory() {
        val userId = getCurrentUserId() ?: return
        isLoading = true
        viewModelScope.launch {
            journalRepository.getJournalHistory(userId)
                .collect { result ->
                    isLoading = false
                    result.onSuccess { entries ->
                        journalHistory.value = entries
                        
                        if (entries.isNotEmpty()) {
                            val first = entries.first()
                            // Set today's entry (or the latest entry) as the current editing entry
                            todayEntry.value = first
                        }
                    }.onFailure { exception ->
                        errorMessage = "Failed to load history: ${exception.message}"
                    }
                }
        }
    }

    fun saveAndAnalyzeJournal(text: String, onComplete: () -> Unit) {
        if (text.trim().isEmpty()) {
            errorMessage = "Journal entry cannot be empty."
            return
        }

        val userId = getCurrentUserId()
        if (userId == null) {
            errorMessage = "User not authenticated."
            return
        }

        isLoading = true
        clearError()

        val existingEntry = todayEntry.value
        val journalId = existingEntry?.id ?: UUID.randomUUID().toString()
        val isNew = existingEntry == null

        viewModelScope.launch {
            // 1. Save journal entry text first
            journalRepository.saveJournalEntry(
                userId = userId,
                journalId = journalId,
                text = text,
                mood = existingEntry?.mood ?: "Calm",
                aiAnalysis = null,
                isNew = isNew
            ).collect { saveResult ->
                saveResult.onSuccess {
                    // 2. Request AI analysis from Gemini
                    val aiResult = aiRepository.analyzeJournal(text)
                    aiResult.onSuccess { jsonStr ->
                        // 3. Parse JSON response and write analysis to Firestore
                        val aiAnalysisMap = parseAnalysisJson(jsonStr)
                        journalRepository.saveJournalEntry(
                            userId = userId,
                            journalId = journalId,
                            text = text,
                            mood = aiAnalysisMap["generalMood"] as? String ?: "Calm",
                            aiAnalysis = aiAnalysisMap,
                            isNew = false
                        ).collect { updateResult ->
                            isLoading = false
                            updateResult.onSuccess {
                                loadJournalHistory()
                                onComplete()
                            }.onFailure { exception ->
                                errorMessage = "Failed to save analysis: ${exception.message}"
                                onComplete()
                            }
                        }
                    }.onFailure { exception ->
                        isLoading = false
                        errorMessage = "AI Analysis failed: ${exception.message}"
                        loadJournalHistory()
                        onComplete()
                    }
                }.onFailure { exception ->
                    isLoading = false
                    errorMessage = "Failed to save journal: ${exception.message}"
                }
            }
        }
    }

    private fun parseAnalysisJson(jsonStr: String): Map<String, Any> {
        return try {
            val jsonObject = JSONObject(jsonStr)
            val suggestionsArray = jsonObject.optJSONArray("practicalSuggestions")
            val suggestionsList = arrayListOf<String>()
            if (suggestionsArray != null) {
                for (i in 0 until suggestionsArray.length()) {
                    suggestionsList.add(suggestionsArray.optString(i))
                }
            }
            
            hashMapOf(
                "detectedEmotion" to jsonObject.optString("detectedEmotion", "Peaceful"),
                "generalMood" to jsonObject.optString("generalMood", "Calm"),
                "possibleStressLevel" to jsonObject.optString("possibleStressLevel", "Low"),
                "shortSummary" to jsonObject.optString("shortSummary", ""),
                "supportiveReflection" to jsonObject.optString("supportiveReflection", "You've had a peaceful day."),
                "practicalSuggestions" to suggestionsList
            )
        } catch (e: Exception) {
            hashMapOf(
                "detectedEmotion" to "Peaceful",
                "generalMood" to "Calm",
                "possibleStressLevel" to "Low",
                "shortSummary" to "Peaceful reflection",
                "supportiveReflection" to "You have written down your reflection safely.",
                "practicalSuggestions" to listOf("Take a deep breath", "Stay hydrated")
            )
        }
    }

    fun deleteJournal(journalId: String) {
        val userId = getCurrentUserId() ?: return
        isLoading = true
        viewModelScope.launch {
            journalRepository.deleteJournalEntry(userId, journalId)
                .collect { result ->
                    isLoading = false
                    result.onSuccess {
                        if (todayEntry.value?.id == journalId) {
                            todayEntry.value = null
                        }
                        loadJournalHistory()
                    }.onFailure { exception ->
                        errorMessage = "Failed to delete: ${exception.message}"
                    }
                }
        }
    }
}
