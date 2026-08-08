package com.manomitra.app.core.voice

import android.content.Context

object VoiceSettings {
    private const val PREFS_NAME = "voice_settings"
    private const val KEY_LANGUAGE = "selected_language"

    fun getSelectedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "Auto") ?: "Auto"
    }

    fun setSelectedLanguage(context: Context, language: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }
}
