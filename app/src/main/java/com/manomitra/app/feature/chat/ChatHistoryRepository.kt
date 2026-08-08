package com.manomitra.app.feature.chat

import kotlinx.coroutines.flow.Flow

interface ChatHistoryRepository {
    fun getMostRecentConversation(userId: String): Flow<Result<Pair<String, List<ChatMessage>>?>>
    fun saveConversationTurn(
        userId: String,
        conversationId: String,
        userMessage: ChatMessage,
        modelMessage: ChatMessage,
        isNewConversation: Boolean
    ): Flow<Result<Unit>>
}
