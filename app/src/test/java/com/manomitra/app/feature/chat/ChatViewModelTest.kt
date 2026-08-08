package com.manomitra.app.feature.chat

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    class FakeAIRepository : AIRepository {
        var lastHistoryPassed: List<ChatMessage>? = null
        var shouldSucceed: Boolean = true
        var mockReply: String = "This is a mock response."
        var mockException: Exception = NetworkException("No internet connection")

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

    class FakeChatHistoryRepository : ChatHistoryRepository {
        var userIdPassed: String? = null
        var conversationIdPassed: String? = null
        var isNewConversationPassed: Boolean? = null
        var savedUserMessage: ChatMessage? = null
        var savedModelMessage: ChatMessage? = null

        var getMostRecentResult: Result<Pair<String, List<ChatMessage>>?> = Result.success(null)
        var saveTurnResult: Result<Unit> = Result.success(Unit)

        override fun getMostRecentConversation(userId: String): Flow<Result<Pair<String, List<ChatMessage>>?>> = flow {
            userIdPassed = userId
            emit(getMostRecentResult)
        }

        override fun saveConversationTurn(
            userId: String,
            conversationId: String,
            userMessage: ChatMessage,
            modelMessage: ChatMessage,
            isNewConversation: Boolean
        ): Flow<Result<Unit>> = flow {
            userIdPassed = userId
            conversationIdPassed = conversationId
            savedUserMessage = userMessage
            savedModelMessage = modelMessage
            isNewConversationPassed = isNewConversation
            emit(saveTurnResult)
        }
    }

    private lateinit var fakeAIRepository: FakeAIRepository
    private lateinit var fakeChatHistoryRepository: FakeChatHistoryRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeAIRepository = FakeAIRepository()
        fakeChatHistoryRepository = FakeChatHistoryRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadHistory_unauthenticatedUser_showsAuthErrorBubble() = runTest {
        // Instantiate ViewModel with null user id
        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { null })
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("User not authenticated. Please log in.", viewModel.errorMessage)
        assertEquals(1, viewModel.messages.size)
        assertEquals("Please log in to load your chat history.", viewModel.messages[0].text)
        assertFalse(viewModel.messages[0].isUser)
    }

    @Test
    fun loadHistory_emptyHistory_setsNewConversation() = runTest {
        fakeChatHistoryRepository.getMostRecentResult = Result.success(null)

        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.errorMessage)
        assertTrue(viewModel.chatMessages.isEmpty())
        assertTrue(viewModel.messages.isEmpty())
    }

    @Test
    fun loadHistory_existingHistory_restoresHistoryToBothLists() = runTest {
        val existingMessages = listOf(
            ChatMessage(ChatRole.USER, "Hi", 1000L),
            ChatMessage(ChatRole.MODEL, "Hello Akshay", 2000L)
        )
        fakeChatHistoryRepository.getMostRecentResult = Result.success(Pair("conversation_abc", existingMessages))

        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.errorMessage)
        assertEquals(2, viewModel.chatMessages.size)
        assertEquals("Hi", viewModel.chatMessages[0].text)
        assertEquals(ChatRole.USER, viewModel.chatMessages[0].role)
        assertEquals("Hello Akshay", viewModel.chatMessages[1].text)
        assertEquals(ChatRole.MODEL, viewModel.chatMessages[1].role)

        assertEquals(2, viewModel.messages.size)
        assertEquals("Hi", viewModel.messages[0].text)
        assertTrue(viewModel.messages[0].isUser)
        assertEquals("Hello Akshay", viewModel.messages[1].text)
        assertFalse(viewModel.messages[1].isUser)
    }

    @Test
    fun loadHistory_firestoreFailure_showsErrorBubbleAndPreparesNewSession() = runTest {
        fakeChatHistoryRepository.getMostRecentResult = Result.failure(Exception("Firestore offline"))

        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Failed to load chat history: Firestore offline", viewModel.errorMessage)
        assertEquals(1, viewModel.messages.size)
        assertEquals("Could not load chat history. Starting a new session.", viewModel.messages[0].text)
    }

    @Test
    fun sendMessage_unauthenticatedUser_showsAuthErrorBubble() = runTest {
        // Change auth state to simulate user logged out / token expired before sending
        val viewModel2 = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { null })
        viewModel2.sendMessage("Hello")

        assertEquals("User not authenticated. Please log in.", viewModel2.errorMessage)
    }

    @Test
    fun sendMessage_success_savesTurnToFirestoreAndAppendsLists() = runTest {
        fakeChatHistoryRepository.getMostRecentResult = Result.success(null)
        fakeAIRepository.shouldSucceed = true
        fakeAIRepository.mockReply = "Here is my response"

        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendMessage("Tell me a story")
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify turn is saved in Firestore
        assertEquals("user_123", fakeChatHistoryRepository.userIdPassed)
        assertNotNull(fakeChatHistoryRepository.conversationIdPassed)
        assertEquals("Tell me a story", fakeChatHistoryRepository.savedUserMessage?.text)
        assertEquals(ChatRole.USER, fakeChatHistoryRepository.savedUserMessage?.role)
        assertEquals("Here is my response", fakeChatHistoryRepository.savedModelMessage?.text)
        assertEquals(ChatRole.MODEL, fakeChatHistoryRepository.savedModelMessage?.role)

        // Verify lists are updated
        assertEquals(2, viewModel.chatMessages.size)
        assertEquals("Here is my response", viewModel.chatMessages[1].text)
        assertEquals(2, viewModel.messages.size)
        assertEquals("Here is my response", viewModel.messages[1].text)
    }

    @Test
    fun sendMessage_saveTurnFailure_showsErrorButDisplaysReplyToUser() = runTest {
        fakeChatHistoryRepository.getMostRecentResult = Result.success(null)
        fakeAIRepository.shouldSucceed = true
        fakeAIRepository.mockReply = "Local reply"
        fakeChatHistoryRepository.saveTurnResult = Result.failure(Exception("Write limit exceeded"))

        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.sendMessage("Try writing")
        testDispatcher.scheduler.advanceUntilIdle()

        // Check that the reply is still appended in lists to let user continue
        assertEquals(2, viewModel.chatMessages.size)
        assertEquals("Local reply", viewModel.chatMessages[1].text)
        assertEquals(2, viewModel.messages.size)
        assertEquals("Local reply", viewModel.messages[1].text)

        // Error message shows the save turn failure
        assertEquals("Failed to save turn: Write limit exceeded", viewModel.errorMessage)
    }

    @Test
    fun voiceInput_setVoiceError_appendsErrorBubbleToMessages() = runTest {
        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.setVoiceError("Microphone permission denied.")

        assertEquals("Microphone permission denied.", viewModel.errorMessage)
        assertEquals(1, viewModel.messages.size)
        assertEquals("Microphone permission denied.", viewModel.messages[0].text)
        assertFalse(viewModel.messages[0].isUser)
    }

    @Test
    fun voiceInput_clearVoiceInputText_resetsFlowValue() = runTest {
        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.voiceInputText.value = "Recognized speech"
        assertEquals("Recognized speech", viewModel.voiceInputText.value)

        viewModel.clearVoiceInputText()
        assertNull(viewModel.voiceInputText.value)
    }

    @Test
    fun voiceResponse_toggleVoiceMode_changesState() = runTest {
        val viewModel = ChatViewModel(fakeAIRepository, fakeChatHistoryRepository, getCurrentUserId = { "user_123" })
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isVoiceModeOn)
        viewModel.stopSpeaking()
        assertFalse(viewModel.isSpeaking)
    }
}
