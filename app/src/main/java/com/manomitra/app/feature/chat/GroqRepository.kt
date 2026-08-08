package com.manomitra.app.feature.chat

import com.manomitra.app.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GroqRepository : AIRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val systemInstructionText = """
        You are Manomitra, a warm, empathetic, and conversational AI mental health companion.
        Your tone is friendly, supportive, concise, and never judgmental. Avoid overly formal or clinical language; instead, speak naturally like a caring friend.
        
        Core instructions:
        1. Automatically detect the language and style of the user's message.
        2. Support English, Kannada, and Kanglish (mixed Kannada + English) fluidly.
        3. Respond in the exact language/style of the user:
           - If the user writes in English, reply in natural conversational English.
           - If the user writes in Kannada (ಕನ್ನಡ), reply in warm, natural Kannada.
           - If the user writes in Kanglish (Kannada written in Latin script or mixed Kannada + English words), reply in the same mixed Kannada + English style naturally.
        4. Adapt instantly if the user switches languages mid-conversation.
        5. If the input is in an unclear or unsupported language, respond gracefully and warmly (in the closest detected language or English) while letting the user know they can speak in English, Kannada, or Kanglish.
    """.trimIndent()

    override suspend fun sendMessage(history: List<ChatMessage>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject()
            root.put("model", "llama-3.3-70b-versatile")
            
            val messagesArray = JSONArray()
            
            val appLanguage = try {
                val context = com.google.firebase.FirebaseApp.getInstance().applicationContext
                com.manomitra.app.core.settings.AppSettings.getAppLanguage(context)
            } catch (e: Exception) {
                "English"
            }
            val latestUserMessage = history.lastOrNull { it.role == ChatRole.USER }?.text ?: ""
            val languageInstruction = """
                
                CRITICAL LANGUAGE INSTRUCTIONS:
                1. Detect the language of the user's latest message: "$latestUserMessage".
                2. The user's preferred app language is set to: $appLanguage.
                3. Respond to the user's latest message in the same language/script they used:
                   - If the latest user message is in Kannada script, respond in Kannada using Kannada script.
                   - If the latest user message is in English, respond in English.
                   - If the latest user message is in Kanglish (Kannada written in Latin script or mixed English + Kannada), respond naturally in Kanglish/colloquial mixed style.
                   - If the latest user message explicitly asks you to reply in a specific language (e.g., "Answer this in Kannada: ..."), prioritize and follow that explicit request.
                   - If the latest user message's language is ambiguous or neutral, default to the user's preferred app language ($appLanguage).
                   - Never respond in Kannada if the user is currently speaking to you in English, and never respond in English if the user is speaking in Kannada script.
            """.trimIndent()
            val systemInstructionTextFinal = systemInstructionText + "\n\n" + languageInstruction

            // Add system instruction first
            val systemMsg = JSONObject()
            systemMsg.put("role", "system")
            systemMsg.put("content", systemInstructionTextFinal)
            messagesArray.put(systemMsg)
            
            // Map history
            for (chatMessage in history) {
                val msg = JSONObject()
                msg.put("role", if (chatMessage.role == ChatRole.USER) "user" else "assistant")
                msg.put("content", chatMessage.text)
                messagesArray.put(msg)
            }
            
            root.put("messages", messagesArray)
            root.put("temperature", 0.7)
            
            val jsonBody = root.toString()
            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
            
            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
                
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val parsedMsg = parseErrorMessage(bodyString, response.message)
                    when (response.code) {
                        401, 403 -> return@withContext Result.failure(InvalidApiKeyException(parsedMsg))
                        429 -> return@withContext Result.failure(RateLimitException(parsedMsg))
                        else -> return@withContext Result.failure(Exception("Groq Server Error (${response.code}): $parsedMsg"))
                    }
                }
                
                if (bodyString.isEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty body received from Groq completions API"))
                }
                
                val responseJson = JSONObject(bodyString)
                val choices = responseJson.optJSONArray("choices")
                if (choices == null || choices.length() == 0) {
                    return@withContext Result.failure(EmptyResponseException("No choices returned from Groq completions API"))
                }
                
                val reply = choices.getJSONObject(0).getJSONObject("message").optString("content")
                if (reply.isNullOrEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty text reply returned from Groq completions API"))
                }
                
                Result.success(reply)
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network/IO error occurred while connecting to Groq API", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzeJournal(text: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Analyze the following journal entry:
                "$text"
                
                Provide your analysis strictly as a JSON object with these keys:
                - detectedEmotion (e.g. "Joy", "Anxiety", "Sadness", "Calm", etc.)
                - generalMood (e.g. "Peaceful", "Stressed", "Happy", "Overwhelmed", etc.)
                - possibleStressLevel (e.g. "Low", "Medium", "High")
                - shortSummary (a one-sentence summary of the entry)
                - supportiveReflection (a warm, empathetic, and supportive reflection)
                - practicalSuggestions (a list of 2-3 concise, practical self-care suggestions)
                
                Remember:
                1. Do not diagnose any medical or mental-health conditions.
                2. Keep the tone supportive, non-judgmental, and concise.
                3. Do not include markdown formatting or backticks around the JSON. Return only the raw JSON.
            """.trimIndent()

            val root = JSONObject()
            root.put("model", "llama-3.3-70b-versatile")
            
            val messagesArray = JSONArray()
            val msg = JSONObject()
            msg.put("role", "user")
            msg.put("content", prompt)
            messagesArray.put(msg)
            
            root.put("messages", messagesArray)
            root.put("temperature", 0.3)
            
            val jsonBody = root.toString()
            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
            
            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
                
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val parsedMsg = parseErrorMessage(bodyString, response.message)
                    when (response.code) {
                        401, 403 -> return@withContext Result.failure(InvalidApiKeyException(parsedMsg))
                        429 -> return@withContext Result.failure(RateLimitException(parsedMsg))
                        else -> return@withContext Result.failure(Exception("Groq Server Error (${response.code}): $parsedMsg"))
                    }
                }
                
                if (bodyString.isEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty body received from Groq completions API"))
                }
                
                val responseJson = JSONObject(bodyString)
                val choices = responseJson.optJSONArray("choices")
                if (choices == null || choices.length() == 0) {
                    return@withContext Result.failure(EmptyResponseException("No choices returned from Groq completions API"))
                }
                
                val reply = choices.getJSONObject(0).getJSONObject("message").optString("content")
                if (reply.isNullOrEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty text reply returned from Groq completions API"))
                }
                
                Result.success(reply)
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network/IO error occurred while connecting to Groq API", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzeMood(moodHistory: List<com.manomitra.app.feature.mood.MoodEntry>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val historyBuilder = StringBuilder()
            for (entry in moodHistory) {
                val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(entry.createdAt))
                historyBuilder.append("Date: $dateStr, Mood: ${entry.mood} (Score: ${entry.moodScore})")
                if (entry.note.isNotEmpty()) {
                    historyBuilder.append(", Note: ${entry.note}")
                }
                historyBuilder.append("\n")
            }

            val prompt = """
                Analyze the following recent mood history:
                $historyBuilder
                
                Provide your analysis strictly as a JSON object with these keys:
                - moodSummary (a brief, supportive summary of the user's recent moods)
                - noticeablePattern (any pattern noticed over the days, e.g. fluctuations, stable periods)
                - positiveObservation (a warm, encouraging positive observation based on the log details)
                - practicalSuggestion (1-2 concise, self-care suggestions for the days ahead)
                
                Guidelines:
                1. Do not diagnose any medical or mental-health conditions.
                2. Do not mention clinical terms, make medical claims, or write alarming conclusions.
                3. Keep the tone warm, conversational, and supportive.
                4. Do not include markdown formatting or backticks around the JSON. Return only the raw JSON.
            """.trimIndent()

            val root = JSONObject()
            root.put("model", "llama-3.3-70b-versatile")
            
            val messagesArray = JSONArray()
            val msg = JSONObject()
            msg.put("role", "user")
            msg.put("content", prompt)
            messagesArray.put(msg)
            
            root.put("messages", messagesArray)
            root.put("temperature", 0.3)
            
            val jsonBody = root.toString()
            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
            
            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
                
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val parsedMsg = parseErrorMessage(bodyString, response.message)
                    when (response.code) {
                        401, 403 -> return@withContext Result.failure(InvalidApiKeyException(parsedMsg))
                        429 -> return@withContext Result.failure(RateLimitException(parsedMsg))
                        else -> return@withContext Result.failure(Exception("Groq Server Error (${response.code}): $parsedMsg"))
                    }
                }
                
                if (bodyString.isEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty body received from Groq completions API"))
                }
                
                val responseJson = JSONObject(bodyString)
                val choices = responseJson.optJSONArray("choices")
                if (choices == null || choices.length() == 0) {
                    return@withContext Result.failure(EmptyResponseException("No choices returned from Groq completions API"))
                }
                
                val reply = choices.getJSONObject(0).getJSONObject("message").optString("content")
                if (reply.isNullOrEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty text reply returned from Groq completions API"))
                }
                
                Result.success(reply)
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network/IO error occurred while connecting to Groq API", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun generateDailyInsight(
        recentMoods: List<com.manomitra.app.feature.mood.MoodEntry>,
        recentJournalSummaries: List<String>,
        language: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (recentMoods.isEmpty() && recentJournalSummaries.isEmpty()) {
                val genericMessage = when (language.lowercase()) {
                    "kannada" -> "ಇಂದು ನಿಮ್ಮ ಬಗ್ಗೆ ಗಮನವಿರಲಿ. ಒಂದು ಸಣ್ಣ ಕ್ಷಣದ ಆತ್ಮಾವಲೋಕನವು ನೀವು ಹೇಗೆ ಭಾವಿಸುತ್ತಿದ್ದೀರಿ ಎಂಬುದನ್ನು ತಿಳಿಯಲು ಸಹಾಯ ಮಾಡುತ್ತದೆ."
                    "auto", "kanglish" -> "Ivattu nimma bagge reflection irali. A small moment of self-reflection can help you check how you are feeling today."
                    else -> "Keep checking in with yourself today. A small moment of reflection can help you notice how you're feeling."
                }
                return@withContext Result.success(genericMessage)
            }

            val dataBuilder = StringBuilder()
            if (recentMoods.isNotEmpty()) {
                dataBuilder.append("Recent Mood Logs:\n")
                recentMoods.take(7).forEach {
                    dataBuilder.append("- Mood: ${it.mood} (Score: ${it.moodScore})\n")
                }
            }
            if (recentJournalSummaries.isNotEmpty()) {
                dataBuilder.append("\nRecent Journal Reflection Highlights:\n")
                recentJournalSummaries.take(5).forEach {
                    dataBuilder.append("- Summary: $it\n")
                }
            }

            val languageInstruction = when (language.lowercase()) {
                "kannada" -> "Write the daily insight in pure Kannada language (using Kannada script)."
                "auto", "kanglish" -> "Write the daily insight in Kanglish (a natural colloquial mix of Kannada and English words in English script)."
                else -> "Write the daily insight in English."
            }

            val prompt = """
                You are Manomitra, a warm and supportive emotional wellness companion.
                Based ONLY on the user's recent logs, generate a highly personalized daily insight.
                
                $dataBuilder
                
                Guidelines:
                1. Length: Exactly 1 to 3 supportive, warm, and practical sentences.
                2. Do not diagnose any medical/mental-health conditions, use clinical terms, or make clinical claims.
                3. Keep it supportive, offering a small practical suggestion.
                4. Language preference: $languageInstruction
                5. Do not include markdown headers or code backticks. Just output the clean text of the insight.
            """.trimIndent()

            val root = JSONObject()
            root.put("model", "llama-3.3-70b-versatile")
            
            val messagesArray = JSONArray()
            val msg = JSONObject()
            msg.put("role", "user")
            msg.put("content", prompt)
            messagesArray.put(msg)
            
            root.put("messages", messagesArray)
            root.put("temperature", 0.7)
            
            val jsonBody = root.toString()
            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
            
            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .post(requestBody)
                .addHeader("Authorization", "Bearer ${BuildConfig.GROQ_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .build()
                
            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    val parsedMsg = parseErrorMessage(bodyString, response.message)
                    when (response.code) {
                        401, 403 -> return@withContext Result.failure(InvalidApiKeyException(parsedMsg))
                        429 -> return@withContext Result.failure(RateLimitException(parsedMsg))
                        else -> return@withContext Result.failure(Exception("Groq Server Error (${response.code}): $parsedMsg"))
                    }
                }
                
                if (bodyString.isEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty body received from Groq completions API"))
                }
                
                val responseJson = JSONObject(bodyString)
                val choices = responseJson.optJSONArray("choices")
                if (choices == null || choices.length() == 0) {
                    return@withContext Result.failure(EmptyResponseException("No choices returned from Groq completions API"))
                }
                
                val reply = choices.getJSONObject(0).getJSONObject("message").optString("content").trim()
                if (reply.isEmpty()) {
                    return@withContext Result.failure(EmptyResponseException("Empty text reply returned from Groq completions API"))
                }
                
                Result.success(reply)
            }
        } catch (e: IOException) {
            Result.failure(NetworkException("Network/IO error occurred while connecting to Groq API", e))
        } catch (e: Exception) {
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
