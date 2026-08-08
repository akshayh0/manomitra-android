package com.manomitra.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    fun deleteAccount(onComplete: (Result<Unit>) -> Unit) {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            onComplete(Result.failure(Exception("No authenticated user")))
            return
        }
        val lastSignIn = currentUser.metadata?.lastSignInTimestamp ?: 0L
        val age = System.currentTimeMillis() - lastSignIn
        if (age > 5 * 60 * 1000) {
            onComplete(Result.failure(Exception("REQUIRES_RECENT_LOGIN")))
            return
        }

        val uid = currentUser.uid
        viewModelScope.launch {
            try {
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(uid)
                
                val deleteRefs = mutableListOf<com.google.firebase.firestore.DocumentReference>()
                
                // 1. Gather journals references
                val journals = userRef.collection("journals").get().await()
                journals.documents.forEach { deleteRefs.add(it.reference) }
                
                // 2. Gather moods references
                val moods = userRef.collection("moods").get().await()
                moods.documents.forEach { deleteRefs.add(it.reference) }
                
                // 3. Gather dailyInsights references
                val dailyInsights = userRef.collection("dailyInsights").get().await()
                dailyInsights.documents.forEach { deleteRefs.add(it.reference) }
                
                // 4. Gather conversations & sub-messages references
                val conversations = userRef.collection("conversations").get().await()
                for (convDoc in conversations.documents) {
                    val messages = convDoc.reference.collection("messages").get().await()
                    messages.documents.forEach { deleteRefs.add(it.reference) }
                    deleteRefs.add(convDoc.reference)
                }
                
                // 5. Add user profile reference
                deleteRefs.add(userRef)
                
                // 6. Commit deletes in chunks of 400 to respect Firestore batch limit (500 max)
                val chunks = deleteRefs.chunked(400)
                for (chunk in chunks) {
                    val batch = db.batch()
                    chunk.forEach { batch.delete(it) }
                    batch.commit().await()
                }
                
                // 7. Delete Firebase Authentication user
                currentUser.delete().await()
                
                _authState.value = AuthState.Unauthenticated
                onComplete(Result.success(Unit))
            } catch (e: Exception) {
                onComplete(Result.failure(e))
            }
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
