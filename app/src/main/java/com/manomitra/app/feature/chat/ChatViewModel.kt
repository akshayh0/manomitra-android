package com.manomitra.app.feature.chat

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manomitra.app.core.voice.VoiceInputManager
import com.manomitra.app.core.voice.TextToSpeechManager
import com.manomitra.app.core.voice.VoiceSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel(
    private val aiRepository: AIRepository = AIRepositoryProvider.get(),
    private val chatHistoryRepository: ChatHistoryRepository = FirestoreChatHistoryRepository(),
    private val getCurrentUserId: () -> String? = { com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid }
) : ViewModel() {

    private val _chatMessages = mutableStateListOf<ChatMessage>()
    val chatMessages: List<ChatMessage> get() = _chatMessages

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    var isTyping by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isListeningVoice by mutableStateOf(false)
        private set

    var isVoiceModeOn by mutableStateOf(false)
        private set

    var isSpeaking by mutableStateOf(false)
        private set

    val voiceInputText = MutableStateFlow<String?>(null)

    private var voiceInputManager: VoiceInputManager? = null
    private var textToSpeechManager: TextToSpeechManager? = null
    private var conversationId: String = ""
    private var isNewConversation: Boolean = true

    init {
        loadMostRecentConversation()
    }

    fun clearError() {
        errorMessage = null
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }

    private fun formatTimestamp(timestamp: Long): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    }

    fun loadMostRecentConversation() {
        val userId = getCurrentUserId()
        if (userId == null) {
            errorMessage = "User not authenticated. Please log in."
            val systemErrorMessage = Message(
                id = UUID.randomUUID().toString(),
                text = "Please log in to load your chat history.",
                isUser = false,
                timestamp = getCurrentTime()
            )
            _messages.add(systemErrorMessage)
            return
        }

        isTyping = true
        clearError()

        viewModelScope.launch {
            chatHistoryRepository.getMostRecentConversation(userId)
                .collect { result ->
                    isTyping = false
                    result.onSuccess { pair ->
                        if (pair != null) {
                            val (loadedConvId, loadedMessages) = pair
                            conversationId = loadedConvId
                            isNewConversation = false

                            _chatMessages.clear()
                            _chatMessages.addAll(loadedMessages)

                            _messages.clear()
                            _messages.addAll(loadedMessages.map { chatMsg ->
                                Message(
                                    id = UUID.randomUUID().toString(),
                                    text = chatMsg.text,
                                    isUser = chatMsg.role == ChatRole.USER,
                                    timestamp = formatTimestamp(chatMsg.timestamp)
                                )
                            })
                        } else {
                            // Empty history
                            conversationId = UUID.randomUUID().toString()
                            isNewConversation = true
                        }
                    }.onFailure { exception ->
                        errorMessage = "Failed to load chat history: ${exception.message}"
                        val systemErrorMessage = Message(
                            id = UUID.randomUUID().toString(),
                            text = "Could not load chat history. Starting a new session.",
                            isUser = false,
                            timestamp = getCurrentTime()
                        )
                        _messages.add(systemErrorMessage)

                        // Fallback to new conversation session
                        conversationId = UUID.randomUUID().toString()
                        isNewConversation = true
                    }
                }
        }
    }

    fun startVoiceInput(context: Context) {
        if (voiceInputManager == null) {
            voiceInputManager = VoiceInputManager(context.applicationContext).apply {
                onResult = { text ->
                    voiceInputText.value = text
                }
                onError = { err ->
                    setVoiceError(err)
                }
                onListeningStateChanged = { listening ->
                    isListeningVoice = listening
                }
            }
        }
        voiceInputManager?.startListening(VoiceSettings.getSelectedLanguage(context))
    }

    fun stopVoiceInput() {
        voiceInputManager?.stopListening()
    }

    fun clearVoiceInputText() {
        voiceInputText.value = null
    }

    fun setVoiceError(errorText: String) {
        errorMessage = errorText
        val systemErrorMessage = Message(
            id = UUID.randomUUID().toString(),
            text = errorText,
            isUser = false,
            timestamp = getCurrentTime()
        )
        _messages.add(systemErrorMessage)
    }

    fun toggleVoiceMode(context: Context) {
        isVoiceModeOn = !isVoiceModeOn
        if (isVoiceModeOn) {
            initializeTTS(context)
        } else {
            stopSpeaking()
        }
    }

    private fun initializeTTS(context: Context) {
        if (textToSpeechManager == null) {
            textToSpeechManager = TextToSpeechManager(
                context = context.applicationContext,
                onInitSuccess = {
                    // Handled internally
                },
                onInitFailed = { err ->
                    setVoiceError(err)
                    isVoiceModeOn = false
                }
            ).apply {
                onCompletion = {
                    this@ChatViewModel.isSpeaking = false
                }
                onError = { err ->
                    setVoiceError(err)
                }
            }
        }
    }

    fun speakResponse(context: Context, text: String) {
        initializeTTS(context)
        val lang = detectLanguage(text)
        isSpeaking = true
        textToSpeechManager?.speak(text, lang)
    }

    fun stopSpeaking() {
        textToSpeechManager?.stop()
        isSpeaking = false
    }

    private fun detectLanguage(text: String): String {
        val hasKannadaScript = text.any { it in '\u0C80'..'\u0CFF' }
        return if (hasKannadaScript) "kn" else "en"
    }

    fun sendMessage(text: String, context: Context? = null) {
        if (text.trim().isEmpty()) return

        val userId = getCurrentUserId()
        if (userId == null) {
            errorMessage = "User not authenticated. Please log in."
            val systemErrorMessage = Message(
                id = UUID.randomUUID().toString(),
                text = "Please log in to chat.",
                isUser = false,
                timestamp = getCurrentTime()
            )
            _messages.add(systemErrorMessage)
            return
        }

        clearError()

        // 1. Maintain conversation context list (ChatMessage)
        val userChatMessage = ChatMessage(
            role = ChatRole.USER,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _chatMessages.add(userChatMessage)

        // 2. Maintain UI presentation list (Message)
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            text = text,
            isUser = true,
            timestamp = getCurrentTime()
        )
        _messages.add(userMessage)

        isTyping = true
        viewModelScope.launch {
            val result = aiRepository.sendMessage(_chatMessages.toList())
            isTyping = false

            result.onSuccess { reply ->
                val modelChatMessage = ChatMessage(
                    role = ChatRole.MODEL,
                    text = reply,
                    timestamp = System.currentTimeMillis()
                )

                // Save both USER and MODEL message atomically in Firestore
                chatHistoryRepository.saveConversationTurn(
                    userId = userId,
                    conversationId = conversationId,
                    userMessage = userChatMessage,
                    modelMessage = modelChatMessage,
                    isNewConversation = isNewConversation
                ).collect { saveResult ->
                    saveResult.onSuccess {
                        // Persisted successfully, update internal lists
                        _chatMessages.add(modelChatMessage)

                        val assistantMessage = Message(
                            id = UUID.randomUUID().toString(),
                            text = reply,
                            isUser = false,
                            timestamp = getCurrentTime()
                        )
                        _messages.add(assistantMessage)
                        
                        isNewConversation = false

                        if (isVoiceModeOn && context != null) {
                            speakResponse(context, reply)
                        }
                    }.onFailure { exception ->
                        // Fallback: Display to user even if DB save fails
                        errorMessage = "Failed to save turn: ${exception.message}"
                        _chatMessages.add(modelChatMessage)

                        val assistantMessage = Message(
                            id = UUID.randomUUID().toString(),
                            text = reply,
                            isUser = false,
                            timestamp = getCurrentTime()
                        )
                        _messages.add(assistantMessage)

                        if (isVoiceModeOn && context != null) {
                            speakResponse(context, reply)
                        }
                    }
                }
            }.onFailure { exception ->
                val errorText = when (exception) {
                    is NetworkException -> "No internet connection. Please check your network and try again."
                    is InvalidApiKeyException -> "Authentication failed: ${exception.message}"
                    is RateLimitException -> "Too many requests: ${exception.message}"
                    is EmptyResponseException -> "Received empty response from the AI."
                    is GeminiServerException -> exception.message ?: "Server error occurred."
                    is GeminiApiException -> exception.message ?: "Generative AI error."
                    else -> exception.message ?: "An unexpected error occurred."
                }
                errorMessage = errorText

                // Add the error message as a chat bubble so the user sees it gracefully
                val systemErrorMessage = Message(
                    id = UUID.randomUUID().toString(),
                    text = errorText,
                    isUser = false,
                    timestamp = getCurrentTime()
                )
                _messages.add(systemErrorMessage)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceInputManager?.destroy()
        voiceInputManager = null
        textToSpeechManager?.shutdown()
        textToSpeechManager = null
    }
}
