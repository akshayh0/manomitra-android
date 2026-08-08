package com.manomitra.app.feature.journal

import com.google.firebase.Timestamp
import com.manomitra.app.feature.chat.AIRepository
import com.manomitra.app.feature.chat.ChatMessage
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
import java.util.Date

@OptIn(ExperimentalCoroutinesApi::class)
class JournalViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeJournalRepository: FakeJournalRepository
    private lateinit var fakeAIRepository: FakeAIRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeJournalRepository = FakeJournalRepository()
        fakeAIRepository = FakeAIRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadJournalHistory_success_updatesFlows() = runTest {
        val entry = JournalEntry(
            id = "journal_1",
            text = "Beautiful day",
            mood = "Calm",
            createdAt = Timestamp(Date())
        )
        fakeJournalRepository.history = listOf(entry)

        val viewModel = JournalViewModel(fakeJournalRepository, fakeAIRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.journalHistory.value.size)
        assertEquals("journal_1", viewModel.journalHistory.value[0].id)
        assertEquals("Beautiful day", viewModel.todayEntry.value?.text)
    }

    @Test
    fun saveAndAnalyzeJournal_success_triggersSaveAndAIAnalysis() = runTest {
        val viewModel = JournalViewModel(fakeJournalRepository, fakeAIRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        fakeAIRepository.mockJsonReply = """
            {
                "detectedEmotion": "Joy",
                "generalMood": "Happy",
                "possibleStressLevel": "Low",
                "shortSummary": "Had a great time",
                "supportiveReflection": "I'm glad you had fun",
                "practicalSuggestions": ["Celebrate success", "Rest"]
            }
        """.trimIndent()

        var completed = false
        viewModel.saveAndAnalyzeJournal("Had a great time today") {
            completed = true
        }
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(completed)
        assertEquals(2, fakeJournalRepository.savedEntries.size)
        val saved = fakeJournalRepository.savedEntries.last()
        assertEquals("Had a great time today", saved.text)
        assertEquals("Happy", saved.mood)
        assertNotNull(saved.aiAnalysis)
        assertEquals("Low", saved.aiAnalysis?.get("possibleStressLevel"))
    }

    @Test
    fun deleteJournal_success_removesFromHistory() = runTest {
        val entry = JournalEntry(id = "journal_1", text = "Old thoughts")
        fakeJournalRepository.history = listOf(entry)

        val viewModel = JournalViewModel(fakeJournalRepository, fakeAIRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("journal_1", viewModel.todayEntry.value?.id)

        viewModel.deleteJournal("journal_1")
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.todayEntry.value)
        assertEquals(0, viewModel.journalHistory.value.size)
    }

    // Fakes
    private class FakeJournalRepository : JournalRepository {
        var history = listOf<JournalEntry>()
        val savedEntries = mutableListOf<JournalEntry>()

        override fun saveJournalEntry(
            userId: String,
            journalId: String,
            text: String,
            mood: String,
            aiAnalysis: Map<String, Any>?,
            isNew: Boolean
        ): Flow<Result<Unit>> = flow {
            val entry = JournalEntry(
                id = journalId,
                text = text,
                mood = mood,
                aiAnalysis = aiAnalysis
            )
            savedEntries.add(entry)
            history = listOf(entry)
            emit(Result.success(Unit))
        }

        override fun getJournalHistory(userId: String): Flow<Result<List<JournalEntry>>> = flow {
            emit(Result.success(history))
        }

        override fun deleteJournalEntry(userId: String, journalId: String): Flow<Result<Unit>> = flow {
            history = history.filterNot { it.id == journalId }
            emit(Result.success(Unit))
        }
    }

    private class FakeAIRepository : AIRepository {
        var mockJsonReply = ""

        override suspend fun sendMessage(history: List<ChatMessage>): Result<String> {
            return Result.success("")
        }

        override suspend fun analyzeJournal(text: String): Result<String> {
            return Result.success(mockJsonReply)
        }

        override suspend fun analyzeMood(moodHistory: List<com.manomitra.app.feature.mood.MoodEntry>): Result<String> {
            return Result.success("")
        }

        override suspend fun generateDailyInsight(
            recentMoods: List<com.manomitra.app.feature.mood.MoodEntry>,
            recentJournalSummaries: List<String>,
            language: String
        ): Result<String> {
            return Result.success("")
        }
    }
}
