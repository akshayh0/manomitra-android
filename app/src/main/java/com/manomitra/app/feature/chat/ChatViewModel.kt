package com.manomitra.app.feature.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel(
    private val aiRepository: AIRepository = GeminiRepository()
) : ViewModel() {

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    var isTyping by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        errorMessage = null
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }

    fun sendMessage(text: String) {
        if (text.trim().isEmpty()) return

        clearError()
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            text = text,
            isUser = true,
            timestamp = getCurrentTime()
        )
        _messages.add(userMessage)

        isTyping = true
        viewModelScope.launch {
            val result = aiRepository.sendMessage(text)
            isTyping = false

            result.onSuccess { reply ->
                val assistantMessage = Message(
                    id = UUID.randomUUID().toString(),
                    text = reply,
                    isUser = false,
                    timestamp = getCurrentTime()
                )
                _messages.add(assistantMessage)
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
}
