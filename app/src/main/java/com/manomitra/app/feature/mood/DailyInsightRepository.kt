package com.manomitra.app.feature.mood

interface DailyInsightRepository {
    suspend fun getDailyInsight(userId: String, dateStr: String): Result<DailyInsight?>
    suspend fun saveDailyInsight(userId: String, insight: DailyInsight): Result<Unit>
}
