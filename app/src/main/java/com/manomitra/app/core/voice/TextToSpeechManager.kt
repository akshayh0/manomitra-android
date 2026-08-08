package com.manomitra.app.core.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TextToSpeechManager(
    private val context: Context,
    private val onInitSuccess: (() -> Unit)? = null,
    private val onInitFailed: ((String) -> Unit)? = null
) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    var isSpeaking: Boolean = false
        private set

    var onCompletion: (() -> Unit)? = null
    var onError: ((String) -> Unit)? = null

    private var lastUtteranceId: String? = null

    init {
        initialize()
    }

    private fun logDebug(tag: String, message: String) {
        if (com.manomitra.app.BuildConfig.DEBUG) {
            try {
                android.util.Log.d(tag, message)
            } catch (e: RuntimeException) {
                println("DEBUG [$tag]: $message")
            }
        }
    }

    private fun logError(tag: String, message: String, throwable: Throwable? = null) {
        try {
            android.util.Log.e(tag, message, throwable)
        } catch (e: RuntimeException) {
            println("ERROR [$tag]: $message - ${throwable?.message}")
        }
    }

    private fun initialize() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                setupUtteranceListener()
                logDiagnostics()
                onInitSuccess?.invoke()
            } else {
                onInitFailed?.invoke("TextToSpeech initialization failed.")
            }
        }
    }

    private fun logDiagnostics() {
        try {
            logDebug("VoiceDiagnostics", "--- TextToSpeech Diagnostics ---")
            val engines = tts?.engines
            logDebug("VoiceDiagnostics", "Available TTS Engines: ${engines?.size ?: 0}")
            for (engine in engines ?: emptyList()) {
                logDebug("VoiceDiagnostics", "  Engine: ${engine.name} - ${engine.label}")
            }
            val defaultEngine = tts?.defaultEngine
            logDebug("VoiceDiagnostics", "Default TTS Engine: $defaultEngine")
            
            val voices = tts?.voices
            logDebug("VoiceDiagnostics", "Total voices available: ${voices?.size ?: 0}")
            var knVoiceCount = 0
            for (voice in voices ?: emptySet()) {
                if (voice.locale.language == "kn") {
                    knVoiceCount++
                    logDebug("VoiceDiagnostics", "  Kannada Voice: name=${voice.name}, locale=${voice.locale}, isNetworkRequired=${voice.isNetworkConnectionRequired}, quality=${voice.quality}")
                }
            }
            logDebug("VoiceDiagnostics", "Kannada Voice Count: $knVoiceCount")
        } catch (e: Exception) {
            logError("VoiceDiagnostics", "Error running TTS diagnostics", e)
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isSpeaking = true
            }

            override fun onDone(utteranceId: String?) {
                if (utteranceId != null && utteranceId == lastUtteranceId) {
                    isSpeaking = false
                    logDebug("VoiceDiagnostics", "TTS playback complete")
                    onCompletion?.invoke()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isSpeaking = false
                onError?.invoke("Speech synthesis error occurred.")
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                isSpeaking = false
                val errorMsg = when (errorCode) {
                    TextToSpeech.ERROR_SYNTHESIS -> "Synthesis error."
                    else -> "Speech synthesis error."
                }
                onError?.invoke(errorMsg)
            }
        })
    }

    fun cleanTextForSpeech(text: String): String {
        var cleaned = text
        // 1. Remove markdown links, keeping only the link text: [Link text](url) -> Link text
        cleaned = cleaned.replace(Regex("\\[(.*?)\\]\\(https?://\\S+\\)"), "$1")
        // 2. Remove standard markdown and other symbols
        cleaned = cleaned.replace(Regex("\\*\\*|\\*"), "") // Bold/Italic
        cleaned = cleaned.replace(Regex("`+"), "")          // Code
        cleaned = cleaned.replace(Regex(">+\\s*"), "")      // Blockquotes
        cleaned = cleaned.replace(Regex("#+\\s*"), "")      // Headers
        cleaned = cleaned.replace(Regex("@"), "")           // At symbol
        cleaned = cleaned.replace(Regex("https?://\\S+"), "") // Leftover URLs
        cleaned = cleaned.replace(Regex("www\\.\\S+"), "")
        
        // 3. Normalize whitespace
        cleaned = cleaned.replace(Regex("\\s+"), " ")
        return cleaned.trim()
    }

    fun speak(text: String, languageCode: String) {
        if (tts == null || !isInitialized) {
            onError?.invoke("TextToSpeech is not initialized.")
            return
        }

        val cleanedText = cleanTextForSpeech(text)
        if (cleanedText.isEmpty()) {
            onError?.invoke("Cannot speak empty text.")
            return
        }

        val maxInputLength = try {
            TextToSpeech.getMaxSpeechInputLength()
        } catch (e: Exception) {
            4000
        }

        val originalCharCount = text.length
        val cleanedCharCount = cleanedText.length

        if (cleanedCharCount <= maxInputLength) {
            logDebug("VoiceDiagnostics", "TTS original character count: $originalCharCount")
            logDebug("VoiceDiagnostics", "TTS cleaned character count: $cleanedCharCount")
            logDebug("VoiceDiagnostics", "TTS chunk count: 1")
            logDebug("VoiceDiagnostics", "TTS chunk 1/1")
            
            speakSingleUtterance(cleanedText, languageCode, TextToSpeech.QUEUE_FLUSH)
            isSpeaking = true
        } else {
            // Chunk at sentence boundaries if input limit is exceeded
            val sentences = cleanedText.split(Regex("(?<=[.!?।])\\s+")).filter { it.trim().isNotEmpty() }
            
            logDebug("VoiceDiagnostics", "TTS original character count: $originalCharCount")
            logDebug("VoiceDiagnostics", "TTS cleaned character count: $cleanedCharCount")
            logDebug("VoiceDiagnostics", "TTS chunk count: ${sentences.size}")

            var first = true
            for ((index, sentence) in sentences.withIndex()) {
                logDebug("VoiceDiagnostics", "TTS chunk ${index + 1}/${sentences.size}")
                val sentenceLang = detectLanguageOfSentence(sentence, languageCode)
                speakSingleUtterance(sentence, sentenceLang, queueMode = if (first) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD)
                first = false
            }
            isSpeaking = true
        }
    }

    private fun detectLanguageOfSentence(sentence: String, defaultLang: String): String {
        val hasKannada = sentence.any { it in '\u0C80'..'\u0CFF' }
        return if (hasKannada) "kn" else "en"
    }

    private fun speakSingleUtterance(sentence: String, languageCode: String, queueMode: Int) {
        val locale = when (languageCode.lowercase()) {
            "kn" -> Locale("kn", "IN")
            "en" -> Locale.US
            else -> Locale.getDefault()
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            if (languageCode.lowercase() == "kn") {
                // Fallback to English US so speech plays successfully
                tts?.setLanguage(Locale.US)
            }
        }

        val utteranceId = System.currentTimeMillis().toString() + "_" + sentence.hashCode()
        lastUtteranceId = utteranceId
        tts?.speak(sentence, queueMode, null, utteranceId)
    }

    fun stop() {
        tts?.stop()
        isSpeaking = false
        lastUtteranceId = null
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
        isInitialized = false
        isSpeaking = false
        lastUtteranceId = null
    }
}
