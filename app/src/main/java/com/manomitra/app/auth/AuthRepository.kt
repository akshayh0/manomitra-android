package com.manomitra.app.auth

import kotlinx.coroutines.flow.Flow

/**
 * AuthRepository
 *
 * Clean Architecture interface defining the core business logic for authentication.
 */
interface AuthRepository {
    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<String>
    suspend fun signInWithEmail(email: String, password: String): Result<String>
    fun signOut()
    fun getCurrentUserEmail(): String?
    fun observeAuthState(): Flow<String?>
}
