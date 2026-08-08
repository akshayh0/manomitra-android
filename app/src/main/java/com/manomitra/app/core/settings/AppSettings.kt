package com.manomitra.app.core.settings

import android.content.Context
import com.manomitra.app.feature.chat.AIProvider

object AppSettings {
    private const val PREFS_NAME = "manomitra_settings"
    private const val KEY_APP_LANGUAGE = "app_language"
    private const val KEY_AI_PROVIDER = "ai_provider"
    private const val KEY_VOICE_MODE = "voice_mode"
    private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
    const val KEY_THEME = "app_theme"

    fun getAppLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_APP_LANGUAGE, "English") ?: "English"
    }

    fun setAppLanguage(context: Context, lang: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_APP_LANGUAGE, lang).apply()
    }

    fun getAIProvider(context: Context): AIProvider {
        val str = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_AI_PROVIDER, AIProvider.GROQ.name) ?: AIProvider.GROQ.name
        return try {
            AIProvider.valueOf(str)
        } catch (e: Exception) {
            AIProvider.GROQ
        }
    }

    fun setAIProvider(context: Context, provider: AIProvider) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_AI_PROVIDER, provider.name).apply()
    }

    fun getVoiceMode(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_VOICE_MODE, "Auto") ?: "Auto"
    }

    fun setVoiceMode(context: Context, mode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_VOICE_MODE, mode).apply()
    }

    fun isNotificationsEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
    }

    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_NOTIFICATIONS_ENABLED, enabled).apply()
    }

    fun getAppTheme(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, "light") ?: "light"
    }

    fun setAppTheme(context: Context, theme: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_THEME, theme).apply()
    }
}
