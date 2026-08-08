package com.manomitra.app.feature.voice

import com.manomitra.app.core.voice.SpeechToTextRepository
import com.manomitra.app.feature.chat.AIRepository
import com.manomitra.app.feature.chat.ChatMessage
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VoiceCompanionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeAIRepository: FakeAIRepository
    private lateinit var fakeSTTRepository: FakeSpeechToTextRepository

    class FakeAIRepository : AIRepository {
        var lastHistoryPassed: List<ChatMessage>? = null
        var shouldSucceed: Boolean = true
        var mockReply: String = "Test reply from Gemini."
        var mockException: Exception = RuntimeException("AI error")

        override suspend fun sendMessage(history: List<ChatMessage>): Result<String> {
            lastHistoryPassed = history
            return if (shouldSucceed) {
                Result.success(mockReply)
            } else {
                Result.failure(mockException)
            }
        }

        override suspend fun analyzeJournal(text: String): Result<String> {
            return Result.success("")
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

    class FakeSpeechToTextRepository : SpeechToTextRepository {
        var audioFilePassed: File? = null
        var languageModePassed: String? = null
        var shouldSucceed: Boolean = true
        var mockTranscription: String = "This is transcribed text."
        var mockException: Exception = RuntimeException("STT Error")

        override suspend fun transcribeAudio(audioFile: File, languageMode: String): Result<String> {
            audioFilePassed = audioFile
            languageModePassed = languageMode
            return if (shouldSucceed) {
                Result.success(mockTranscription)
            } else {
                Result.failure(mockException)
            }
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAIRepository = FakeAIRepository()
        fakeSTTRepository = FakeSpeechToTextRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_isIdle() {
        val viewModel = VoiceCompanionViewModel(fakeAIRepository, fakeSTTRepository)
        assertEquals(VoiceState.IDLE, viewModel.currentState)
        assertEquals("", viewModel.userTranscript)
        assertEquals("", viewModel.aiTranscript)
        assertNull(viewModel.errorMessage)
        assertFalse(viewModel.isMuted)
        assertTrue(viewModel.isSpeakerOn)
    }

    @Test
    fun handleUserSpeechResult_success_updatesTranscriptsAndState() = runTest {
        val viewModel = VoiceCompanionViewModel(fakeAIRepository, fakeSTTRepository)
        fakeAIRepository.mockReply = "Empathetic companion response."

        viewModel.handleUserSpeechResult(null, "I am feeling lonely")
        
        // Assert state changes to THINKING immediately
        assertEquals(VoiceState.THINKING, viewModel.currentState)
        
        testDispatcher.scheduler.advanceUntilIdle()

        // After job completion:
        assertEquals("I am feeling lonely", viewModel.userTranscript)
        assertEquals("Empathetic companion response.", viewModel.aiTranscript)
        // Since speaker is on, state should transition to SPEAKING (emulated TTS playing)
        assertEquals(VoiceState.SPEAKING, viewModel.currentState)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun handleUserSpeechResult_failure_setsErrorAndIdleState() = runTest {
        val viewModel = VoiceCompanionViewModel(fakeAIRepository, fakeSTTRepository)
        fakeAIRepository.shouldSucceed = false
        fakeAIRepository.mockException = RuntimeException("Service down")

        viewModel.handleUserSpeechResult(null, "Hello companion")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Service down", viewModel.errorMessage)
        assertEquals(VoiceState.ERROR, viewModel.currentState)
    }

    @Test
    fun handleSpeechError_updatesStateAndMessage() {
        val viewModel = VoiceCompanionViewModel(fakeAIRepository, fakeSTTRepository)
        viewModel.handleSpeechError(null, "Recognizer error occurred.")
        assertEquals("Recognizer error occurred.", viewModel.errorMessage)
        assertEquals(VoiceState.ERROR, viewModel.currentState)
    }
}
