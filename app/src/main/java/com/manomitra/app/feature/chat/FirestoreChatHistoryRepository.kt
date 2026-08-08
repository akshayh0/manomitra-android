package com.manomitra.app.feature.chat

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreChatHistoryRepository : ChatHistoryRepository {

    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    override fun getMostRecentConversation(userId: String): Flow<Result<Pair<String, List<ChatMessage>>?>> = flow {
        try {
            val conversationsSnapshot = firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            if (conversationsSnapshot.isEmpty) {
                emit(Result.success(null))
                return@flow
            }

            val conversationDoc = conversationsSnapshot.documents.first()
            val conversationId = conversationDoc.id

            val messagesSnapshot = conversationDoc.reference.collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()

            val messages = messagesSnapshot.documents.mapNotNull { doc ->
                val roleStr = doc.getString("role") ?: return@mapNotNull null
                val text = doc.getString("text") ?: return@mapNotNull null
                val timestamp = doc.getLong("timestamp") ?: return@mapNotNull null

                val role = try {
                    ChatRole.valueOf(roleStr)
                } catch (e: IllegalArgumentException) {
                    ChatRole.USER
                }

                ChatMessage(role = role, text = text, timestamp = timestamp)
            }

            emit(Result.success(Pair(conversationId, messages)))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun saveConversationTurn(
        userId: String,
        conversationId: String,
        userMessage: ChatMessage,
        modelMessage: ChatMessage,
        isNewConversation: Boolean
    ): Flow<Result<Unit>> = flow {
        try {
            val conversationRef = firestore.collection("users")
                .document(userId)
                .collection("conversations")
                .document(conversationId)

            val batch = firestore.batch()

            val conversationMap = hashMapOf<String, Any>(
                "updatedAt" to FieldValue.serverTimestamp()
            )
            if (isNewConversation) {
                conversationMap["createdAt"] = FieldValue.serverTimestamp()
            }
            batch.set(conversationRef, conversationMap, SetOptions.merge())

            val userMessageRef = conversationRef.collection("messages").document()
            val userMessageMap = hashMapOf(
                "role" to userMessage.role.name,
                "text" to userMessage.text,
                "timestamp" to userMessage.timestamp
            )
            batch.set(userMessageRef, userMessageMap)

            val modelMessageRef = conversationRef.collection("messages").document()
            val modelMessageMap = hashMapOf(
                "role" to modelMessage.role.name,
                "text" to modelMessage.text,
                "timestamp" to modelMessage.timestamp
            )
            batch.set(modelMessageRef, modelMessageMap)

            batch.commit().await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
