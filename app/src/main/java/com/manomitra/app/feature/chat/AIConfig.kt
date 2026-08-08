package com.manomitra.app.feature.chat

import android.content.Context
import com.manomitra.app.core.settings.AppSettings

enum class AIProvider {
    GROQ,
    GEMINI
}

object AIConfig {
    var ACTIVE_PROVIDER = AIProvider.GROQ

    fun init(context: Context) {
        ACTIVE_PROVIDER = AppSettings.getAIProvider(context)
    }

    fun setProvider(context: Context, provider: AIProvider) {
        ACTIVE_PROVIDER = provider
        AppSettings.setAIProvider(context, provider)
    }
}
