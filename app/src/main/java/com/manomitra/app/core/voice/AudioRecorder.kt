package com.manomitra.app.core.voice

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.util.Log
import java.io.File

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    
    var isRecording: Boolean = false
        private set

    private fun logDebug(tag: String, message: String) {
        if (com.manomitra.app.BuildConfig.DEBUG) {
            try {
                Log.d(tag, message)
            } catch (e: Exception) {
                println("DEBUG [$tag]: $message")
            }
        }
    }

    private fun logError(tag: String, message: String, throwable: Throwable? = null) {
        try {
            Log.e(tag, message, throwable)
        } catch (e: Exception) {
            println("ERROR [$tag]: $message - ${throwable?.message}")
        }
    }

    fun start(): File? {
        Log.d("VoiceDiagnostics", "recording started")
        if (isRecording) {
            stop()
        }

        try {
            outputFile = File.createTempFile("manomitra_voice_", ".m4a", context.cacheDir)
            
            mediaRecorder = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioChannels(1) // Mono channel
                setAudioSamplingRate(16000) // 16000 Hz sample rate
                setAudioEncodingBitRate(32000) // 32 kbps
                setOutputFile(outputFile?.absolutePath)
                prepare()
                start()
            }
            
            isRecording = true
            return outputFile
        } catch (e: Exception) {
            logError("AudioRecorder", "Failed to start recording", e)
            cleanup()
            return null
        }
    }

    fun stop(): File? {
        if (!isRecording) return null
        
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            logError("AudioRecorder", "Error stopping mediaRecorder (probably stopped too early)", e)
        } finally {
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
        
        Log.d("VoiceDiagnostics", "recording stopped")
        verifyRecordedFile()
        return outputFile
    }

    private fun verifyRecordedFile() {
        val file = outputFile
        if (file == null || !file.exists()) {
            Log.d("VoiceDiagnostics", "  file size: 0 bytes")
            return
        }

        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L

            Log.d("VoiceDiagnostics", "  duration: ${durationMs}ms")
            Log.d("VoiceDiagnostics", "  file size: ${file.length()} bytes")
            Log.d("VoiceDiagnostics", "  MIME type: audio/mp4")
        } catch (e: Exception) {
            logError("VoiceDiagnostics", "Failed to verify audio encoding properties: ${e.message}")
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun cancel() {
        logDebug("AudioRecorder", "cancel requested")
        cleanup()
    }

    private fun cleanup() {
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            // ignore
        } finally {
            mediaRecorder?.release()
            mediaRecorder = null
        }
        
        isRecording = false
        
        outputFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }
        outputFile = null
        logDebug("AudioRecorder", "Resources cleaned up, temp audio deleted")
    }
}
