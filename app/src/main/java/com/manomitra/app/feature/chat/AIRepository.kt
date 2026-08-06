package com.manomitra.app.feature.chat

interface AIRepository {
    suspend fun sendMessage(message: String): Result<String>
}
