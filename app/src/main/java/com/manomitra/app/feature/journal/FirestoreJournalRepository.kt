package com.manomitra.app.feature.journal

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreJournalRepository : JournalRepository {

    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    override fun saveJournalEntry(
        userId: String,
        journalId: String,
        text: String,
        mood: String,
        aiAnalysis: Map<String, Any>?,
        isNew: Boolean
    ): Flow<Result<Unit>> = flow {
        try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("journals")
                .document(journalId)

            val data = hashMapOf<String, Any>(
                "text" to text,
                "mood" to mood,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            if (isNew) {
                data["createdAt"] = FieldValue.serverTimestamp()
            }

            if (aiAnalysis != null) {
                data["aiAnalysis"] = aiAnalysis
                data["aiAnalyzedAt"] = FieldValue.serverTimestamp()
            }

            docRef.set(data, com.google.firebase.firestore.SetOptions.merge()).await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getJournalHistory(userId: String): Flow<Result<List<JournalEntry>>> = flow {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("journals")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val entries = snapshot.documents.mapNotNull { doc ->
                val text = doc.getString("text") ?: return@mapNotNull null
                val mood = doc.getString("mood") ?: ""
                val createdAt = doc.getTimestamp("createdAt")
                val updatedAt = doc.getTimestamp("updatedAt")
                val aiAnalysis = doc.get("aiAnalysis") as? Map<String, Any>
                val aiAnalyzedAt = doc.getTimestamp("aiAnalyzedAt")

                JournalEntry(
                    id = doc.id,
                    text = text,
                    mood = mood,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    aiAnalysis = aiAnalysis,
                    aiAnalyzedAt = aiAnalyzedAt
                )
            }
            emit(Result.success(entries))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun deleteJournalEntry(userId: String, journalId: String): Flow<Result<Unit>> = flow {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("journals")
                .document(journalId)
                .delete()
                .await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
