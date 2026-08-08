package com.manomitra.app.feature.mood

data class DailyInsight(
    val date: String = "",
    val insight: String = "",
    val moodSummary: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val provider: String = ""
)
