package com.manomitra.app.feature.mood

import com.manomitra.app.feature.chat.AIRepository
import com.manomitra.app.feature.chat.ChatMessage
import com.manomitra.app.feature.journal.JournalEntry
import com.manomitra.app.feature.journal.JournalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeMoodRepository: FakeMoodRepository
    private lateinit var fakeDailyInsightRepository: FakeDailyInsightRepository
    private lateinit var fakeJournalRepository: FakeJournalRepository
    private lateinit var fakePrimaryAIRepository: FakeAIRepository
    private lateinit var fakeFallbackAIRepository: FakeAIRepository

    class FakeMoodRepository : MoodRepository {
        var savedMoods = mutableListOf<MoodEntry>()
        var getHistoryResult: Result<List<MoodEntry>> = Result.success(emptyList())
        var shouldSucceedSave = true
        var shouldSucceedDelete = true

        override suspend fun saveMood(userId: String, entry: MoodEntry): Result<Unit> {
            return if (shouldSucceedSave) {
                savedMoods.add(entry)
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException("Firestore error"))
            }
        }

        override suspend fun getRecentMoods(userId: String, limit: Int): Result<List<MoodEntry>> {
            return getHistoryResult.map { it.take(limit) }
        }

        override suspend fun getMoodHistory(userId: String): Result<List<MoodEntry>> {
            return getHistoryResult
        }

        override suspend fun deleteMood(userId: String, moodId: String): Result<Unit> {
            return if (shouldSucceedDelete) {
                savedMoods.removeAll { it.id == moodId }
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException("Delete error"))
            }
        }
    }

    class FakeDailyInsightRepository : DailyInsightRepository {
        var savedInsights = mutableMapOf<String, DailyInsight>()
        var shouldSucceedGet = true
        var shouldSucceedSave = true
        var getCallCount = 0
        var saveCallCount = 0

        override suspend fun getDailyInsight(userId: String, dateStr: String): Result<DailyInsight?> {
            getCallCount++
            return if (shouldSucceedGet) {
                Result.success(savedInsights["$userId/$dateStr"])
            } else {
                Result.failure(RuntimeException("Firestore read error"))
            }
        }

        override suspend fun saveDailyInsight(userId: String, insight: DailyInsight): Result<Unit> {
            saveCallCount++
            return if (shouldSucceedSave) {
                savedInsights["$userId/${insight.date}"] = insight
                Result.success(Unit)
            } else {
                Result.failure(RuntimeException("Firestore write error"))
            }
        }
    }

    class FakeJournalRepository : JournalRepository {
        var mockHistory = mutableListOf<JournalEntry>()

        override fun saveJournalEntry(
            userId: String,
            journalId: String,
            text: String,
            mood: String,
            aiAnalysis: Map<String, Any>?,
            isNew: Boolean
        ): Flow<Result<Unit>> = flow {
            emit(Result.success(Unit))
        }

        override fun getJournalHistory(userId: String): Flow<Result<List<JournalEntry>>> = flow {
            emit(Result.success(mockHistory))
        }

        override fun deleteJournalEntry(userId: String, journalId: String): Flow<Result<Unit>> = flow {
            emit(Result.success(Unit))
        }
    }

    class FakeAIRepository : AIRepository {
        var mockReply = "AI Mood Analysis response"
        var mockInsightReply = "Supportive daily insight text."
        var shouldSucceed = true
        var shouldSucceedInsight = true
        var generateDailyInsightCallCount = 0

        override suspend fun sendMessage(history: List<ChatMessage>): Result<String> = Result.success("")
        override suspend fun analyzeJournal(text: String): Result<String> = Result.success("")
        override suspend fun analyzeMood(moodHistory: List<MoodEntry>): Result<String> {
            return if (shouldSucceed) Result.success(mockReply) else Result.failure(RuntimeException("AI quota exceeded"))
        }

        override suspend fun generateDailyInsight(
            recentMoods: List<MoodEntry>,
            recentJournalSummaries: List<String>,
            language: String
        ): Result<String> {
            generateDailyInsightCallCount++
            if (recentMoods.isEmpty() && recentJournalSummaries.isEmpty()) {
                return Result.success("Keep checking in with yourself today. A small moment of reflection can help you notice how you're feeling.")
            }
            return if (shouldSucceedInsight) {
                Result.success(mockInsightReply)
            } else {
                Result.failure(RuntimeException("Groq quota exceeded"))
            }
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeMoodRepository = FakeMoodRepository()
        fakeDailyInsightRepository = FakeDailyInsightRepository()
        fakeJournalRepository = FakeJournalRepository()
        fakePrimaryAIRepository = FakeAIRepository()
        fakeFallbackAIRepository = FakeAIRepository()
        
        // Populated history to bypass empty-fallback trigger by default
        fakeMoodRepository.getHistoryResult = Result.success(listOf(
            MoodEntry(mood = "HAPPY", createdAt = System.currentTimeMillis())
        ))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun moodScoreMapping_isCorrect() {
        assertEquals(5, MoodEntry.mapToScore(MoodType.VERY_HAPPY))
        assertEquals(4, MoodEntry.mapToScore(MoodType.HAPPY))
        assertEquals(3, MoodEntry.mapToScore(MoodType.NEUTRAL))
        assertEquals(2, MoodEntry.mapToScore(MoodType.SAD))
        assertEquals(1, MoodEntry.mapToScore(MoodType.VERY_SAD))
        
        assertEquals(MoodType.VERY_HAPPY, MoodEntry.fromString("happy"))
        assertEquals(MoodType.HAPPY, MoodEntry.fromString("calm"))
        assertEquals(MoodType.NEUTRAL, MoodEntry.fromString("neutral"))
        assertEquals(MoodType.SAD, MoodEntry.fromString("sad"))
        assertEquals(MoodType.VERY_SAD, MoodEntry.fromString("stressed"))
    }

    @Test
    fun saveMood_success_reloadsHistory() = runTest {
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Mock empty for test explicitly
        viewModel.deleteMood("")
        fakeMoodRepository.savedMoods.clear()
        fakeMoodRepository.getHistoryResult = Result.success(emptyList())
        viewModel.loadMoodHistory()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(viewModel.recentMoods.value.isEmpty())
        
        viewModel.saveMood("happy")
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals(1, fakeMoodRepository.savedMoods.size)
        assertEquals("VERY_HAPPY", fakeMoodRepository.savedMoods[0].mood)
        assertEquals(5, fakeMoodRepository.savedMoods[0].moodScore)
    }

    @Test
    fun loadHistory_failure_setsError() = runTest {
        fakeMoodRepository.getHistoryResult = Result.failure(RuntimeException("No network"))
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("No network", viewModel.error.value)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun requestAIAnalysis_success_setsAnalysisResponse() = runTest {
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.requestMoodAnalysis()
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertFalse(viewModel.isAnalyzing.value)
        assertEquals("AI Mood Analysis response", viewModel.aiAnalysis.value)
    }

    @Test
    fun streakCalculation_correctlyComputesConsecutiveDays() {
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        
        assertEquals(0, viewModel.calculateStreak(emptyList()))
        
        val cal = Calendar.getInstance()
        val today = cal.timeInMillis
        
        cal.add(Calendar.DATE, -1)
        val yesterday = cal.timeInMillis
        
        cal.add(Calendar.DATE, -1)
        val dayBefore = cal.timeInMillis
        
        val entries = listOf(
            MoodEntry(mood = "HAPPY", createdAt = today),
            MoodEntry(mood = "NEUTRAL", createdAt = yesterday),
            MoodEntry(mood = "VERY_HAPPY", createdAt = dayBefore)
        )
        assertEquals(3, viewModel.calculateStreak(entries))
        
        val entriesWithDuplicates = listOf(
            MoodEntry(mood = "HAPPY", createdAt = today),
            MoodEntry(mood = "NEUTRAL", createdAt = today),
            MoodEntry(mood = "NEUTRAL", createdAt = yesterday)
        )
        assertEquals(2, viewModel.calculateStreak(entriesWithDuplicates))
        
        val brokenEntries = listOf(
            MoodEntry(mood = "HAPPY", createdAt = today),
            MoodEntry(mood = "VERY_HAPPY", createdAt = dayBefore)
        )
        assertEquals(1, viewModel.calculateStreak(brokenEntries))
    }

    // Daily Insight unit tests

    @Test
    fun dailyInsight_exists_noAIRequest() = runTest {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        fakeDailyInsightRepository.savedInsights["test_user/$todayStr"] = DailyInsight(
            date = todayStr,
            insight = "Cached wellness suggestion.",
            moodSummary = "HAPPY"
        )

        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Cached wellness suggestion.", viewModel.dailyInsight.value?.insight)
        assertEquals(0, fakePrimaryAIRepository.generateDailyInsightCallCount)
    }

    @Test
    fun dailyInsight_notExists_generatesAndSaves() = runTest {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Supportive daily insight text.", viewModel.dailyInsight.value?.insight)
        assertEquals(1, fakePrimaryAIRepository.generateDailyInsightCallCount)
        assertEquals(1, fakeDailyInsightRepository.saveCallCount)
        assertNotNull(fakeDailyInsightRepository.savedInsights["test_user/$todayStr"])
    }

    @Test
    fun dailyInsight_successfulAIGeneration() = runTest {
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isInsightLoading.value)
        assertNull(viewModel.insightError.value)
        assertEquals("Supportive daily insight text.", viewModel.dailyInsight.value?.insight)
    }

    @Test
    fun dailyInsight_groqFailure_geminiFallback() = runTest {
        fakePrimaryAIRepository.shouldSucceedInsight = false
        fakeFallbackAIRepository.mockInsightReply = "Gemini fallback insight"
        
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Gemini fallback insight", viewModel.dailyInsight.value?.insight)
        assertEquals("Gemini", viewModel.dailyInsight.value?.provider)
        assertEquals(1, fakePrimaryAIRepository.generateDailyInsightCallCount)
        assertEquals(1, fakeFallbackAIRepository.generateDailyInsightCallCount)
    }

    @Test
    fun dailyInsight_bothAIProvidersFail() = runTest {
        fakePrimaryAIRepository.shouldSucceedInsight = false
        fakeFallbackAIRepository.shouldSucceedInsight = false
        
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.dailyInsight.value)
        assertEquals("Groq quota exceeded", viewModel.insightError.value)
    }

    @Test
    fun dailyInsight_firestoreFailure() = runTest {
        fakeDailyInsightRepository.shouldSucceedGet = false
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        // It should still generate and show the insight, ignoring firestore failures
        assertEquals("Supportive daily insight text.", viewModel.dailyInsight.value?.insight)
    }

    @Test
    fun dailyInsight_emptyMoodHistory() = runTest {
        fakeMoodRepository.getHistoryResult = Result.success(emptyList())
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        // When both moods and journals are empty, it returns the generic response without calling AI
        assertEquals("Keep checking in with yourself today. A small moment of reflection can help you notice how you're feeling.", viewModel.dailyInsight.value?.insight)
        assertEquals(1, fakePrimaryAIRepository.generateDailyInsightCallCount)
    }

    @Test
    fun dailyInsight_emptyJournalSummaries() = runTest {
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Supportive daily insight text.", viewModel.dailyInsight.value?.insight)
        assertEquals(1, fakePrimaryAIRepository.generateDailyInsightCallCount)
    }

    @Test
    fun dailyInsight_oneGenerationPerDay() = runTest {
        val viewModel = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "test_user" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, fakePrimaryAIRepository.generateDailyInsightCallCount)
    }

    @Test
    fun dailyInsight_differentUsers_separateCache() = runTest {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        fakeDailyInsightRepository.savedInsights["user_a/$todayStr"] = DailyInsight(
            date = todayStr,
            insight = "Insight A",
            moodSummary = "HAPPY"
        )

        val viewModelB = MoodViewModel(fakeMoodRepository, fakeDailyInsightRepository, fakeJournalRepository, fakePrimaryAIRepository, fakeFallbackAIRepository, { "user_b" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModelB.loadDailyInsight()
        testDispatcher.scheduler.advanceUntilIdle()

        // User B doesn't read User A's cache, B generates new one
        assertEquals("Supportive daily insight text.", viewModelB.dailyInsight.value?.insight)
        assertEquals(1, fakePrimaryAIRepository.generateDailyInsightCallCount)
    }
}
