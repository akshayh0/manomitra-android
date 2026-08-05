package com.manomitra.app.auth

/**
 * AuthState
 *
 * Represents the authentication UI state for MVVM architecture.
 */
sealed interface AuthState {
    object Unauthenticated : AuthState
    object Loading : AuthState
    data class Authenticated(val userEmail: String) : AuthState
    data class Error(val message: String) : AuthState
}
