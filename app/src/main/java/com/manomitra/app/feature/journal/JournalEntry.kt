package com.manomitra.app.feature.journal

import com.google.firebase.Timestamp

data class JournalEntry(
    val id: String = "",
    val text: String = "",
    val mood: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val aiAnalysis: Map<String, Any>? = null,
    val aiAnalyzedAt: Timestamp? = null
)
