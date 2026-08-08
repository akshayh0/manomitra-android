package com.manomitra.app.feature.chat

import com.manomitra.app.core.settings.AppSettings

object AIRepositoryProvider {
    fun get(): AIRepository {
        val context = try {
            com.google.firebase.FirebaseApp.getInstance().applicationContext
        } catch (e: Exception) {
            null
        }
        val provider = if (context != null) {
            AppSettings.getAIProvider(context)
        } else {
            AIConfig.ACTIVE_PROVIDER
        }
        return when (provider) {
            AIProvider.GROQ -> GroqRepository()
            AIProvider.GEMINI -> GeminiRepository()
        }
    }
}
