package com.manomitra.app.feature.chat

interface AIRepository {
    suspend fun sendMessage(history: List<ChatMessage>): Result<String>
    suspend fun analyzeJournal(text: String): Result<String>
    suspend fun analyzeMood(moodHistory: List<com.manomitra.app.feature.mood.MoodEntry>): Result<String>
    suspend fun generateDailyInsight(
        recentMoods: List<com.manomitra.app.feature.mood.MoodEntry>,
        recentJournalSummaries: List<String>,
        language: String = "English"
    ): Result<String>
}
