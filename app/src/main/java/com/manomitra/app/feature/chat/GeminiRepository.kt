package com.manomitra.app.feature.chat

import com.google.genai.Client
import com.google.genai.errors.ApiException
import com.google.genai.errors.ClientException
import com.google.genai.errors.ServerException
import com.google.genai.errors.GenAiIOException
import com.manomitra.app.BuildConfig
import org.json.JSONObject
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository : AIRepository {

    private val client = Client.builder()
        .apiKey(BuildConfig.GEMINI_API_KEY)
        .build()

    override suspend fun sendMessage(message: String): Result<String> {
        return try {
            val response = withContext(Dispatchers.IO) {
                client.models.generateContent("gemini-3.6-flash", message, null)
            }
            val reply = response.text()
            if (reply.isNullOrEmpty()) {
                Result.failure(EmptyResponseException("Empty response received from Gemini API"))
            } else {
                Result.success(reply)
            }
        } catch (e: ClientException) {
            val rawMsg = e.message ?: ""
            val parsedMsg = parseErrorMessage(rawMsg)
            when {
                rawMsg.contains("API_KEY_INVALID", ignoreCase = true) || 
                rawMsg.contains("API key", ignoreCase = true) ||
                rawMsg.contains("403", ignoreCase = true) -> {
                    Result.failure(InvalidApiKeyException(parsedMsg, e))
                }
                rawMsg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) || 
                rawMsg.contains("rate limit", ignoreCase = true) ||
                rawMsg.contains("429", ignoreCase = true) -> {
                    Result.failure(RateLimitException(parsedMsg, e))
                }
                else -> {
                    Result.failure(GeminiServerException(parsedMsg, e))
                }
            }
        } catch (e: ServerException) {
            val rawMsg = e.message ?: ""
            Result.failure(GeminiServerException(parseErrorMessage(rawMsg), e))
        } catch (e: GenAiIOException) {
            Result.failure(NetworkException("Network/IO error occurred while connecting to Gemini API", e))
        } catch (e: ApiException) {
            val rawMsg = e.message ?: ""
            Result.failure(GeminiApiException(parseErrorMessage(rawMsg), e))
        } catch (e: Exception) {
            val rawMsg = e.message ?: ""
            val parsedMsg = parseErrorMessage(rawMsg)
            if (parsedMsg != rawMsg) {
                Result.failure(GeminiApiException(parsedMsg, e))
            } else {
                Result.failure(e)
            }
        }
    }

    private fun parseErrorMessage(msg: String): String {
        val jsonStart = msg.indexOf("{")
        val jsonEnd = msg.lastIndexOf("}")
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            val trimmed = msg.substring(jsonStart, jsonEnd + 1).trim()
            try {
                val jsonObject = JSONObject(trimmed)
                val errorObj = jsonObject.optJSONObject("error")
                if (errorObj != null) {
                    val parsedMsg = errorObj.optString("message")
                    if (!parsedMsg.isNullOrEmpty()) {
                        return parsedMsg
                    }
                }
            } catch (e: Exception) {
                // Fallback to raw message
            }
        }
        return msg
    }
}

// Custom Exceptions
class EmptyResponseException(message: String) : Exception(message)
class InvalidApiKeyException(message: String, cause: Throwable? = null) : Exception(message, cause)
class RateLimitException(message: String, cause: Throwable? = null) : Exception(message, cause)
class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause)
class GeminiServerException(message: String, cause: Throwable? = null) : Exception(message, cause)
class GeminiApiException(message: String, cause: Throwable? = null) : Exception(message, cause)
