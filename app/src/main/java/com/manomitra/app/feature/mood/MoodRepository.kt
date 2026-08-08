package com.manomitra.app.feature.mood

interface MoodRepository {
    suspend fun saveMood(userId: String, entry: MoodEntry): Result<Unit>
    suspend fun getRecentMoods(userId: String, limit: Int): Result<List<MoodEntry>>
    suspend fun getMoodHistory(userId: String): Result<List<MoodEntry>>
    suspend fun deleteMood(userId: String, moodId: String): Result<Unit>
}
