package com.manomitra.app.auth

import kotlinx.coroutines.flow.Flow

/**
 * FirestoreRepository
 *
 * Clean Architecture interface for Cloud Firestore user profile operations using Flow.
 */
interface FirestoreRepository {
    fun saveUserProfileFlow(user: User): Flow<Result<Unit>>
    fun getUserProfileFlow(uid: String): Flow<Result<User?>>
    fun updateLastLoginFlow(uid: String): Flow<Result<Unit>>
}
