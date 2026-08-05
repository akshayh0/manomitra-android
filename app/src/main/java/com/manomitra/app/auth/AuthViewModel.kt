package com.manomitra.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * AuthViewModel
 *
 * Exposes StateFlow of AuthState to the UI and triggers authentication processes.
 * Designed to be clean architecture and MVVM compliant, as well as Hilt-ready.
 */
class AuthViewModel(
    private val repository: AuthRepository = FirebaseAuthRepository(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepositoryImpl()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Track session status at startup and emit accordingly
        viewModelScope.launch {
            repository.observeAuthState().collectLatest { email ->
                if (email != null) {
                    _authState.value = AuthState.Authenticated(email)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    /**
     * Attempts sign in with email and password.
     */
    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signInWithEmail(email, password)
            result.fold(
                onSuccess = { userEmail ->
                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val uid = currentUser.uid
                        firestoreRepository.getUserProfileFlow(uid).collectLatest { profileResult ->
                            profileResult.fold(
                                onSuccess = { existingUser ->
                                    if (existingUser != null) {
                                        firestoreRepository.updateLastLoginFlow(uid).collectLatest { updateResult ->
                                            updateResult.fold(
                                                onSuccess = {
                                                    _authState.value = AuthState.Authenticated(userEmail)
                                                },
                                                onFailure = { throwable ->
                                                    _authState.value = AuthState.Error(throwable.message ?: "Failed to update last login in Firestore")
                                                }
                                            )
                                        }
                                    } else {
                                        val newUserProfile = User(
                                            uid = uid,
                                            name = currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "User",
                                            email = currentUser.email ?: email,
                                            profileImage = "",
                                            onboardingCompleted = true,
                                            moodStreak = 0,
                                            journalCount = 0
                                        )
                                        firestoreRepository.saveUserProfileFlow(newUserProfile).collectLatest { saveResult ->
                                            saveResult.fold(
                                                onSuccess = {
                                                    _authState.value = AuthState.Authenticated(userEmail)
                                                },
                                                onFailure = { throwable ->
                                                    _authState.value = AuthState.Error(throwable.message ?: "Failed to create user profile in Firestore")
                                                }
                                            )
                                        }
                                    }
                                },
                                onFailure = { throwable ->
                                    _authState.value = AuthState.Error(throwable.message ?: "Failed to fetch user profile from Firestore")
                                }
                            )
                        }
                    } else {
                        _authState.value = AuthState.Authenticated(userEmail)
                    }
                },
                onFailure = { throwable ->
                    _authState.value = AuthState.Error(throwable.message ?: "Authentication failed")
                }
            )
        }
    }

    /**
     * Attempts registration with email, password, and name.
     */
    fun signUp(email: String, password: String, name: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val result = repository.signUpWithEmail(email, password, name)
            result.fold(
                onSuccess = { userEmail ->
                    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    if (currentUser != null) {
                        val userProfile = User(
                            uid = currentUser.uid,
                            name = name,
                            email = currentUser.email ?: email,
                            profileImage = "",
                            onboardingCompleted = true,
                            moodStreak = 0,
                            journalCount = 0
                        )
                        firestoreRepository.saveUserProfileFlow(userProfile).collectLatest { firestoreResult ->
                            firestoreResult.fold(
                                onSuccess = {
                                    _authState.value = AuthState.Authenticated(userEmail)
                                },
                                onFailure = { throwable ->
                                    _authState.value = AuthState.Error(throwable.message ?: "Failed to save user profile in Firestore")
                                }
                            )
                        }
                    } else {
                        _authState.value = AuthState.Authenticated(userEmail)
                    }
                },
                onFailure = { throwable ->
                    _authState.value = AuthState.Error(throwable.message ?: "Registration failed")
                }
            )
        }
    }

    /**
     * Signs the current user out.
     */
    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _authState.value = AuthState.Unauthenticated
        }
    }

    /**
     * Clears any active error state back to Unauthenticated.
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}
