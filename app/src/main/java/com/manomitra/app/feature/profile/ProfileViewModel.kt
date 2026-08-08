package com.manomitra.app.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manomitra.app.auth.FirestoreRepository
import com.manomitra.app.auth.FirestoreRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepositoryImpl(),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val currentUser = firebaseAuth.currentUser

    private val _displayName = MutableStateFlow(
        currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Manomitra User"
    )
    val displayName: StateFlow<String> = _displayName.asStateFlow()

    private val _email = MutableStateFlow(currentUser?.email ?: "user@manomitra.com")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _profileImage = MutableStateFlow<String?>(null)
    val profileImage: StateFlow<String?> = _profileImage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        val uid = currentUser?.uid ?: return
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            firestoreRepository.getUserProfileFlow(uid).collectLatest { result ->
                _isLoading.value = false
                result.fold(
                    onSuccess = { user ->
                        if (user != null) {
                            if (user.name.isNotEmpty()) {
                                _displayName.value = user.name
                            }
                            _profileImage.value = user.profileImage
                        }
                    },
                    onFailure = { throwable ->
                        _errorMessage.value = throwable.message ?: "Failed to load user profile"
                    }
                )
            }
        }
    }

    fun saveUserProfile(
        context: android.content.Context,
        newName: String,
        selectedImageUri: android.net.Uri?,
        removePhoto: Boolean,
        onComplete: (Result<Unit>) -> Unit
    ) {
        val uid = currentUser?.uid ?: return
        _isSaving.value = true
        viewModelScope.launch {
            try {
                var finalImagePath = _profileImage.value ?: ""

                if (removePhoto) {
                    finalImagePath = ""
                    // Delete local file if exists
                    val localFile = java.io.File(context.filesDir, "profile_pic_${uid}.png")
                    if (localFile.exists()) {
                        localFile.delete()
                    }
                } else if (selectedImageUri != null) {
                    val localFile = java.io.File(context.filesDir, "profile_pic_${uid}.png")
                    val copySuccess = context.contentResolver.openInputStream(selectedImageUri)?.use { input ->
                        localFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                        true
                    } ?: false

                    if (copySuccess) {
                        finalImagePath = localFile.absolutePath
                    }
                }

                // Update Firebase Auth display name
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    displayName = newName
                }
                currentUser.updateProfile(profileUpdates).await()

                // Update Firestore profile
                firestoreRepository.updateUserProfileFlow(uid, newName, finalImagePath).collectLatest { result ->
                    _isSaving.value = false
                    result.fold(
                        onSuccess = {
                            _displayName.value = newName
                            _profileImage.value = finalImagePath
                            onComplete(Result.success(Unit))
                        },
                        onFailure = { throwable ->
                            onComplete(Result.failure(throwable))
                        }
                    )
                }
            } catch (e: Exception) {
                _isSaving.value = false
                onComplete(Result.failure(e))
            }
        }
    }
}
