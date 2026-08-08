package com.manomitra.app.core.settings

import android.content.Context
import android.content.SharedPreferences
import com.manomitra.app.feature.chat.AIProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*

class AppSettingsTest {

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        mockPrefs = mock(SharedPreferences::class.java)
        mockEditor = mock(SharedPreferences.Editor::class.java)

        `when`(mockContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockPrefs)
        `when`(mockPrefs.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor)
    }

    @Test
    fun testLanguagePersistence() {
        `when`(mockPrefs.getString(eq("app_language"), anyString())).thenReturn("Kannada")
        
        val lang = AppSettings.getAppLanguage(mockContext)
        assertEquals("Kannada", lang)

        AppSettings.setAppLanguage(mockContext, "Auto")
        verify(mockEditor).putString("app_language", "Auto")
        verify(mockEditor).apply()
    }

    @Test
    fun testAIProviderPersistence() {
        `when`(mockPrefs.getString(eq("ai_provider"), anyString())).thenReturn(AIProvider.GEMINI.name)
        
        val provider = AppSettings.getAIProvider(mockContext)
        assertEquals(AIProvider.GEMINI, provider)

        AppSettings.setAIProvider(mockContext, AIProvider.GROQ)
        verify(mockEditor).putString("ai_provider", AIProvider.GROQ.name)
        verify(mockEditor).apply()
    }

    @Test
    fun testVoicePreferencePersistence() {
        `when`(mockPrefs.getString(eq("voice_mode"), anyString())).thenReturn("English")
        
        val mode = AppSettings.getVoiceMode(mockContext)
        assertEquals("English", mode)

        AppSettings.setVoiceMode(mockContext, "Kannada")
        verify(mockEditor).putString("voice_mode", "Kannada")
        verify(mockEditor).apply()
    }

    @Test
    fun testNotificationPreferencePersistence() {
        `when`(mockPrefs.getBoolean(eq("notifications_enabled"), anyBoolean())).thenReturn(false)
        
        val enabled = AppSettings.isNotificationsEnabled(mockContext)
        assertFalse(enabled)

        AppSettings.setNotificationsEnabled(mockContext, true)
        verify(mockEditor).putBoolean("notifications_enabled", true)
        verify(mockEditor).apply()
    }
}
