package com.manomitra.app.core.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class VoiceInputManager(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    
    var isListening: Boolean = false
        private set

    var onResult: ((String) -> Unit)? = null
    var onPartialResult: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onListeningStateChanged: ((Boolean) -> Unit)? = null

    init {
        logDiagnostics(context)
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

    private fun logDiagnostics(context: Context) {
        try {
            logDebug("VoiceDiagnostics", "--- SpeechRecognizer Diagnostics ---")
            val available = SpeechRecognizer.isRecognitionAvailable(context)
            logDebug("VoiceDiagnostics", "SpeechRecognizer.isRecognitionAvailable: $available")
            
            val defaultRecognizer = android.provider.Settings.Secure.getString(
                context.contentResolver,
                "voice_recognition_service"
            )
            logDebug("VoiceDiagnostics", "Default Secure Voice Recognizer Service: $defaultRecognizer")
            
            val intent = Intent("android.speech.RecognitionService")
            val services = context.packageManager.queryIntentServices(intent, 0)
            logDebug("VoiceDiagnostics", "Installed Recognition Services: ${services.size}")
            for (service in services) {
                logDebug("VoiceDiagnostics", "  Package: ${service.serviceInfo.packageName}, Service: ${service.serviceInfo.name}")
            }
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val onDeviceAvailable = SpeechRecognizer.isOnDeviceRecognitionAvailable(context)
                logDebug("VoiceDiagnostics", "SpeechRecognizer.isOnDeviceRecognitionAvailable: $onDeviceAvailable")
            }
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                logDebug("VoiceDiagnostics", "Android API level supports EXTRA_ENABLE_LANGUAGE_SWITCH (API >= 34)")
            } else {
                logDebug("VoiceDiagnostics", "Android API level < 34")
            }
        } catch (e: Exception) {
            logError("VoiceDiagnostics", "Error running SpeechRecognizer diagnostics", e)
        }
    }

    private fun setupListener() {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                logDebug("VoiceInputManager", "onReadyForSpeech")
                isListening = true
                onListeningStateChanged?.invoke(true)
            }

            override fun onBeginningOfSpeech() {
                logDebug("VoiceInputManager", "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                logDebug("VoiceInputManager", "onEndOfSpeech")
                isListening = false
                onListeningStateChanged?.invoke(false)
            }

            override fun onError(error: Int) {
                isListening = false
                onListeningStateChanged?.invoke(false)
                val errorMessage = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error (ERROR_AUDIO)."
                    SpeechRecognizer.ERROR_CLIENT -> "Speech recognition client error (ERROR_CLIENT)."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions to record audio (ERROR_INSUFFICIENT_PERMISSIONS)."
                    SpeechRecognizer.ERROR_NETWORK -> "Network error occurred (ERROR_NETWORK)."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout (ERROR_NETWORK_TIMEOUT)."
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected (ERROR_NO_MATCH)."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognizer is busy (ERROR_RECOGNIZER_BUSY)."
                    SpeechRecognizer.ERROR_SERVER -> "Speech recognition server error (ERROR_SERVER)."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected (ERROR_SPEECH_TIMEOUT)."
                    else -> "Speech recognition error occurred (code: $error)."
                }
                logError("VoiceInputManager", "onError callback: code=$error ($errorMessage)")
                onError?.invoke(errorMessage)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    logDebug("VoiceInputManager", "onResults: ${matches[0]}")
                    onResult?.invoke(matches[0])
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    logDebug("VoiceInputManager", "onPartialResults: ${matches[0]}")
                    onPartialResult?.invoke(matches[0])
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening(mode: String = "Auto") {
        logDebug("VoiceInputManager", "startListening requested in mode: $mode")
        stopListening()
        destroyRecognizer()

        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            setupListener()
        } else {
            onError?.invoke("Speech recognition is not available on this device.")
            return
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

            when (mode.lowercase()) {
                "english", "en", "en-in" -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
                    logDebug("VoiceInputManager", "Configuring RecognizerIntent with Locale: en-IN")
                }
                "kannada", "kn", "kn-in" -> {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "kn-IN")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "kn-IN")
                    logDebug("VoiceInputManager", "Configuring RecognizerIntent with Locale: kn-IN")
                }
                else -> { // Auto mode
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        putExtra("android.speech.extra.ENABLE_LANGUAGE_SWITCH", true)
                        putExtra("android.speech.extra.LANGUAGE_SWITCH_ALLOWED_LANGUAGES", arrayOf("kn-IN", "en-IN"))
                        logDebug("VoiceInputManager", "Configuring RecognizerIntent with Auto Language Switching (kn-IN, en-IN)")
                    } else {
                        logDebug("VoiceInputManager", "Configuring RecognizerIntent with default system Locale: ${Locale.getDefault().language}")
                    }
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().language)
                }
            }
        }

        try {
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            logError("VoiceInputManager", "Failed to startListening", e)
            onError?.invoke("Failed to start speech recognition: ${e.message}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            logError("VoiceInputManager", "Failed to stopListening", e)
        }
        isListening = false
        onListeningStateChanged?.invoke(false)
    }

    private fun destroyRecognizer() {
        try {
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            logError("VoiceInputManager", "Failed to destroy speechRecognizer", e)
        }
        speechRecognizer = null
    }

    fun destroy() {
        destroyRecognizer()
    }
}
