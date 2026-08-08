package com.manomitra.app.core.voice

import android.media.MediaMetadataRetriever
import com.manomitra.app.BuildConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroqSpeechToTextRepository : SpeechToTextRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

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

    override suspend fun transcribeAudio(audioFile: File, languageMode: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!audioFile.exists()) {
                return@withContext Result.failure(IOException("Audio file does not exist: ${audioFile.absolutePath}"))
            }

            // Retrieve audio duration
            var durationMs = 0L
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(audioFile.absolutePath)
                durationMs = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
            } catch (e: Exception) {
                // ignore
            } finally {
                try {
                    retriever.release()
                } catch (e: Exception) {
                    // ignore
                }
            }

            val langParam = when (languageMode) {
                "Kannada" -> "kn"
                "English" -> "en"
                else -> "omitted"
            }
            android.util.Log.d("VoiceDiagnostics", "audio duration: ${durationMs}ms")
            android.util.Log.d("VoiceDiagnostics", "file size: ${audioFile.length()} bytes")
            android.util.Log.d("VoiceDiagnostics", "language: $langParam")
            android.util.Log.d("VoiceDiagnostics", "model: whisper-large-v3")

            val fileBody = audioFile.asRequestBody("audio/mp4".toMediaTypeOrNull())
            val requestBodyBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", audioFile.name, fileBody)
                .addFormDataPart("model", "whisper-large-v3")
                .addFormDataPart("response_format", "verbose_json")
                .addFormDataPart("temperature", "0")
                
            // Set prompt guidelines
            val prompt = when (languageMode) {
                "Kannada" -> "Preserve Kannada script. Do not translate. Keep English loanwords like stress, work, college."
                "English" -> "Transcribe the spoken English accurately. Do not translate."
                else -> "Preserve the original spoken language and spelling. Do not translate. Keep mixed Kannada and English words exactly as spoken."
            }
            requestBodyBuilder.addFormDataPart("prompt", prompt)

            when (languageMode) {
                "Kannada" -> requestBodyBuilder.addFormDataPart("language", "kn")
                "English" -> requestBodyBuilder.addFormDataPart("language", "en")
                // Auto mode -> omit the language parameter to allow auto-detection
            }
            
            val requestBody = requestBodyBuilder.build()
            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/audio/transcriptions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                .build()
                
            val startTime = System.currentTimeMillis()
            client.newCall(request).execute().use { response ->
                val responseTime = System.currentTimeMillis() - startTime
                val bodyString = response.body?.string() ?: ""
                
                android.util.Log.d("VoiceDiagnostics", "transcription completed")
                android.util.Log.d("VoiceDiagnostics", "response duration: ${responseTime}ms")
                
                if (!response.isSuccessful) {
                    val errorMsg = parseErrorMessage(bodyString, response.message)
                    logError("VoiceDiagnostics", "Groq Whisper STT API Error: $errorMsg")
                    return@withContext Result.failure(Exception("Groq STT Server Error (${response.code}): $errorMsg"))
                }
                
                if (bodyString.isEmpty()) {
                    logError("VoiceDiagnostics", "Groq Whisper STT response body is empty")
                    return@withContext Result.failure(Exception("Empty response from Groq STT API"))
                }
                
                val json = JSONObject(bodyString)
                val text = json.optString("text")
                if (text.isNullOrEmpty()) {
                    logError("VoiceDiagnostics", "No transcription text in response: $bodyString")
                    return@withContext Result.failure(Exception("No transcription text returned from Groq STT API"))
                }
                
                val segments = json.optJSONArray("segments")
                if (segments != null && segments.length() > 0) {
                    var totalNoSpeechProb = 0.0
                    var totalAvgLogprob = 0.0
                    var totalCompressionRatio = 0.0
                    for (i in 0 until segments.length()) {
                        val segment = segments.getJSONObject(i)
                        val start = segment.optDouble("start", 0.0)
                        val end = segment.optDouble("end", 0.0)
                        val noSpeechProb = segment.optDouble("no_speech_prob", 0.0)
                        val avgLogprob = segment.optDouble("avg_logprob", 0.0)
                        val compressionRatio = segment.optDouble("compression_ratio", 0.0)
                        
                        totalNoSpeechProb += noSpeechProb
                        totalAvgLogprob += avgLogprob
                        totalCompressionRatio += compressionRatio
                        
                        logDebug("VoiceDiagnostics", "  Segment $i [${start}s - ${end}s]: avg_logprob=$avgLogprob, no_speech_prob=$noSpeechProb, compression_ratio=$compressionRatio")
                    }
                    val avgNoSpeech = totalNoSpeechProb / segments.length()
                    val avgLogprob = totalAvgLogprob / segments.length()
                    val avgComp = totalCompressionRatio / segments.length()
                    
                    logDebug("VoiceDiagnostics", "  Overall Stats: avg_logprob=$avgLogprob, avg_no_speech_prob=$avgNoSpeech, avg_compression_ratio=$avgComp")
                }
                
                logDebug("VoiceDiagnostics", "  Transcription Length: ${text.length} chars")
                Result.success(text)
            }
        } catch (e: IOException) {
            logError("VoiceDiagnostics", "Network error in STT", e)
            Result.failure(Exception("Network error occurred during transcription", e))
        } catch (e: Exception) {
            logError("VoiceDiagnostics", "Unexpected error in STT", e)
            Result.failure(e)
        }
    }

    private fun parseErrorMessage(body: String, defaultMsg: String): String {
        if (body.isEmpty()) return defaultMsg
        try {
            val json = JSONObject(body)
            val errorObj = json.optJSONObject("error")
            if (errorObj != null) {
                val parsed = errorObj.optString("message")
                if (!parsed.isNullOrEmpty()) {
                    return parsed
                }
            }
        } catch (e: Exception) {
            // ignore
        }
        return defaultMsg
    }
}
