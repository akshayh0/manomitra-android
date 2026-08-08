package com.manomitra.app.feature.voice

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manomitra.app.core.voice.AudioRecorder
import com.manomitra.app.core.voice.GroqSpeechToTextRepository
import com.manomitra.app.core.voice.SpeechToTextRepository
import com.manomitra.app.core.voice.TextToSpeechManager
import com.manomitra.app.core.voice.VoiceInputManager
import com.manomitra.app.core.voice.VoiceSettings
import com.manomitra.app.feature.chat.AIRepository
import com.manomitra.app.feature.chat.AIRepositoryProvider
import com.manomitra.app.feature.chat.ChatMessage
import com.manomitra.app.feature.chat.ChatRole
import java.io.File
import kotlinx.coroutines.launch

class VoiceCompanionViewModel(
    private val aiRepository: AIRepository = AIRepositoryProvider.get(),
    private val sttRepository: SpeechToTextRepository = GroqSpeechToTextRepository()
) : ViewModel() {

    var currentState by mutableStateOf(VoiceState.IDLE)
        private set

    var userTranscript by mutableStateOf("")
        private set

    var aiTranscript by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isMuted by mutableStateOf(false)
        private set

    var isSpeakerOn by mutableStateOf(true)
        private set

    private var voiceInputManager: VoiceInputManager? = null
    private var textToSpeechManager: TextToSpeechManager? = null
    private var audioRecorder: AudioRecorder? = null
    private var appContext: Context? = null

    private val chatMessages = mutableListOf<ChatMessage>()
    private var tempFile: File? = null
    
    // Flag to track if we fell back to native Android speech recognition
    private var isUsingFallbackRecognizer = false

    private fun logDebug(message: String) {
        if (com.manomitra.app.BuildConfig.DEBUG) {
            try {
                android.util.Log.d("VoiceCompanionViewModel", message)
            } catch (e: RuntimeException) {
                println("DEBUG: $message")
            }
        }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        try {
            android.util.Log.e("VoiceCompanionViewModel", message, throwable)
        } catch (e: RuntimeException) {
            println("ERROR: $message - ${throwable?.message}")
        }
    }

    fun initialize(context: Context) {
        appContext = context.applicationContext
        if (audioRecorder == null) {
            audioRecorder = AudioRecorder(context.applicationContext)
        }

        if (voiceInputManager == null) {
            voiceInputManager = VoiceInputManager(context.applicationContext).apply {
                onResult = { text ->
                    if (isUsingFallbackRecognizer) {
                        handleUserSpeechResult(context, text)
                    }
                }
                onPartialResult = { text ->
                    if (isUsingFallbackRecognizer && currentState == VoiceState.LISTENING) {
                        userTranscript = text
                    }
                }
                onError = { err ->
                    if (isUsingFallbackRecognizer) {
                        handleSpeechError(context, err)
                    }
                }
                onListeningStateChanged = { listening ->
                    if (isUsingFallbackRecognizer) {
                        if (listening) {
                            currentState = VoiceState.LISTENING
                        } else if (currentState == VoiceState.LISTENING) {
                            currentState = VoiceState.IDLE
                        }
                    }
                }
            }
        }

        if (textToSpeechManager == null) {
            textToSpeechManager = TextToSpeechManager(
                context = context.applicationContext,
                onInitSuccess = {
                    // Ready
                },
                onInitFailed = { err ->
                    logError("TTS Init failed: $err")
                    errorMessage = err
                }
            ).apply {
                onCompletion = {
                    logDebug("TTS speaking completed")
                    currentState = VoiceState.IDLE
                    if (isSpeakerOn && !isMuted) {
                        startListening(context)
                    }
                }
                onError = { err ->
                    logError("TTS error: $err")
                    errorMessage = err
                    currentState = VoiceState.IDLE
                }
            }
        }
    }

    fun startListening(context: Context) {
        if (isMuted) return
        if (currentState == VoiceState.LISTENING || 
            currentState == VoiceState.TRANSCRIBING || 
            currentState == VoiceState.THINKING || 
            currentState == VoiceState.SPEAKING) {
            logDebug("startListening ignored: already active in state $currentState")
            return
        }
        errorMessage = null
        stopSpeaking()
        initialize(context)

        val selectedLanguage = VoiceSettings.getSelectedLanguage(context)
        logDebug("startListening: selectedLanguage=$selectedLanguage")

        isUsingFallbackRecognizer = false
        currentState = VoiceState.LISTENING
        
        // Start MediaRecorder audio capture
        tempFile = audioRecorder?.start()
        if (tempFile == null) {
            logError("AudioRecorder failed to start. Falling back to native Android speech recognition.")
            startFallbackRecognizer(context, selectedLanguage)
        }
    }

    fun stopListening() {
        if (currentState != VoiceState.LISTENING) {
            logDebug("stopListening ignored: current state is $currentState")
            return
        }
        if (isUsingFallbackRecognizer) {
            voiceInputManager?.stopListening()
            if (currentState == VoiceState.LISTENING) {
                currentState = VoiceState.IDLE
            }
            return
        }

        val recordedFile = audioRecorder?.stop()
        if (currentState == VoiceState.LISTENING) {
            currentState = VoiceState.TRANSCRIBING
            
            val audioFile = recordedFile ?: return
            if (!audioFile.exists() || audioFile.length() == 0L) {
                logError("Finalized audio file is missing or empty!")
                errorMessage = "Audio recording failed: empty file."
                currentState = VoiceState.ERROR
                return
            }
            
            val context = appContext ?: return
            val selectedLanguage = VoiceSettings.getSelectedLanguage(context)
            
            // Run STT transcription asynchronously
            viewModelScope.launch {
                // Additional safety buffer to ensure complete filesystem sync
                kotlinx.coroutines.delay(500)
                
                // Verify again after delay to be absolutely certain
                if (!audioFile.exists() || audioFile.length() == 0L) {
                    logError("Audio file vanished or is empty after safety delay!")
                    errorMessage = "Audio recording failed: invalid file."
                    currentState = VoiceState.ERROR
                    return@launch
                }
                
                logDebug("Sending audio to Groq Whisper for transcription...")
                val result = sttRepository.transcribeAudio(audioFile, selectedLanguage)
                
                // Always delete temp file to protect user privacy
                deleteTempFile()

                result.onSuccess { transcribedText ->
                    logDebug("Groq Whisper Transcription Success: $transcribedText")
                    handleUserSpeechResult(context, transcribedText)
                }.onFailure { exception ->
                    logError("Groq Whisper Transcription Failed", exception)
                    errorMessage = "Groq Whisper failed: ${exception.message}. Falling back to native SpeechRecognizer."
                    
                    // Fallback to native Android speech recognition
                    startFallbackRecognizer(context, selectedLanguage)
                }
            }
        }
    }

    private fun startFallbackRecognizer(context: Context, language: String) {
        isUsingFallbackRecognizer = true
        currentState = VoiceState.LISTENING
        voiceInputManager?.startListening(language)
    }

    fun stopSpeaking() {
        textToSpeechManager?.stop()
        if (currentState == VoiceState.SPEAKING) {
            currentState = VoiceState.IDLE
        }
    }

    fun toggleMute(context: Context) {
        isMuted = !isMuted
        logDebug("toggleMute: isMuted=$isMuted")
        if (isMuted) {
            if (isUsingFallbackRecognizer) {
                voiceInputManager?.stopListening()
            } else {
                audioRecorder?.cancel()
                deleteTempFile()
            }
            stopSpeaking()
            currentState = VoiceState.IDLE
        } else {
            startListening(context)
        }
    }

    fun toggleSpeaker() {
        isSpeakerOn = !isSpeakerOn
        logDebug("toggleSpeaker: isSpeakerOn=$isSpeakerOn")
        if (!isSpeakerOn) {
            stopSpeaking()
        }
    }

    internal fun handleUserSpeechResult(context: Context?, text: String) {
        if (text.trim().isEmpty()) return
        if (currentState == VoiceState.THINKING || currentState == VoiceState.SPEAKING) {
            logDebug("handleUserSpeechResult ignored: already thinking or speaking")
            return
        }
        logDebug("handleUserSpeechResult: user text=$text")
        userTranscript = text

        val userMsg = ChatMessage(
            role = ChatRole.USER,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        chatMessages.add(userMsg)

        currentState = VoiceState.THINKING
        viewModelScope.launch {
            val result = aiRepository.sendMessage(chatMessages)
            result.onSuccess { reply ->
                logDebug("AI response: $reply")
                val modelMsg = ChatMessage(
                    role = ChatRole.MODEL,
                    text = reply,
                    timestamp = System.currentTimeMillis()
                )
                chatMessages.add(modelMsg)

                aiTranscript = reply
                
                if (isSpeakerOn && !isMuted) {
                    currentState = VoiceState.SPEAKING
                    val detectLang = detectLanguage(reply)
                    textToSpeechManager?.speak(reply, detectLang)
                } else {
                    currentState = VoiceState.IDLE
                }
            }.onFailure { exception ->
                logError("AI error", exception)
                errorMessage = exception.message ?: "Failed to get AI response"
                currentState = VoiceState.ERROR
            }
        }
    }

    internal fun handleSpeechError(context: Context?, error: String) {
        logDebug("handleSpeechError: $error")
        if (error.contains("No speech detected", ignoreCase = true)) {
            if (currentState == VoiceState.LISTENING && !isMuted) {
                // Resume listening automatically
                context?.let {
                    voiceInputManager?.startListening(VoiceSettings.getSelectedLanguage(it))
                }
            }
        } else {
            errorMessage = error
            currentState = VoiceState.ERROR
        }
    }

    private fun deleteTempFile() {
        try {
            tempFile?.let {
                if (it.exists()) {
                    it.delete()
                }
            }
        } catch (e: Exception) {
            logError("Error deleting temporary audio file", e)
        } finally {
            tempFile = null
        }
    }

    private fun detectLanguage(text: String): String {
        val hasKannadaScript = text.any { it in '\u0C80'..'\u0CFF' }
        return if (hasKannadaScript) "kn" else "en"
    }

    fun shutdown() {
        logDebug("shutdown resources")
        audioRecorder?.cancel()
        audioRecorder = null
        deleteTempFile()
        voiceInputManager?.destroy()
        voiceInputManager = null
        textToSpeechManager?.shutdown()
        textToSpeechManager = null
        currentState = VoiceState.IDLE
    }

    override fun onCleared() {
        super.onCleared()
        shutdown()
    }
}
