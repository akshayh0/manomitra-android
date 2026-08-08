package com.manomitra.app.feature.chat

enum class ChatRole {
    USER, MODEL
}

data class ChatMessage(
    val role: ChatRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
