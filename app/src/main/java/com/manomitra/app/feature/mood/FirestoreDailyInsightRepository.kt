package com.manomitra.app.feature.mood

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FirestoreDailyInsightRepository : DailyInsightRepository {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getDailyInsight(userId: String, dateStr: String): Result<DailyInsight?> = withContext(Dispatchers.IO) {
        try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("dailyInsights")
                .document(dateStr)
                .get()
                .await()
            if (doc.exists()) {
                val entry = doc.toObject(DailyInsight::class.java)
                Result.success(entry)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveDailyInsight(userId: String, insight: DailyInsight): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firestore.collection("users")
                .document(userId)
                .collection("dailyInsights")
                .document(insight.date)
                .set(insight)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
