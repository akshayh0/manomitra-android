package com.manomitra.app.feature.mood

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreMoodRepository : MoodRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun saveMood(userId: String, entry: MoodEntry): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val userMoodsCollection = firestore.collection("users").document(userId).collection("moods")
            val docId = entry.id.ifEmpty { userMoodsCollection.document().id }
            val finalEntry = entry.copy(
                id = docId,
                createdAt = if (entry.createdAt == 0L) System.currentTimeMillis() else entry.createdAt,
                updatedAt = System.currentTimeMillis()
            )
            userMoodsCollection.document(docId).set(finalEntry).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentMoods(userId: String, limit: Int): Result<List<MoodEntry>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("moods")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()
            val moodsList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MoodEntry::class.java)
            }
            Result.success(moodsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMoodHistory(userId: String): Result<List<MoodEntry>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("moods")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val moodsList = snapshot.documents.mapNotNull { doc ->
                doc.toObject(MoodEntry::class.java)
            }
            Result.success(moodsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteMood(userId: String, moodId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("moods")
                .document(moodId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
