package com.manomitra.app.feature.journal

import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun saveJournalEntry(
        userId: String,
        journalId: String,
        text: String,
        mood: String,
        aiAnalysis: Map<String, Any>? = null,
        isNew: Boolean
    ): Flow<Result<Unit>>

    fun getJournalHistory(userId: String): Flow<Result<List<JournalEntry>>>

    fun deleteJournalEntry(userId: String, journalId: String): Flow<Result<Unit>>
}
