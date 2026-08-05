package com.manomitra.app.auth

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

/**
 * FirestoreRepositoryImpl
 *
 * Implements FirestoreRepository using the Firebase Firestore SDK,
 * utilizing Kotlin Coroutines + Flow, and writing server timestamps.
 */
class FirestoreRepositoryImpl : FirestoreRepository {

    private val firestore: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    override fun saveUserProfileFlow(user: User): Flow<Result<Unit>> = flow {
        try {
            // Write complete user profile with server timestamps
            val userMap = hashMapOf(
                "uid" to user.uid,
                "name" to user.name,
                "email" to user.email,
                "profileImage" to user.profileImage,
                "createdAt" to FieldValue.serverTimestamp(),
                "onboardingCompleted" to user.onboardingCompleted,
                "moodStreak" to user.moodStreak,
                "journalCount" to user.journalCount,
                "lastLogin" to FieldValue.serverTimestamp()
            )
            firestore.collection("users")
                .document(user.uid)
                .set(userMap)
                .await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getUserProfileFlow(uid: String): Flow<Result<User?>> = flow {
        try {
            val snapshot = firestore.collection("users")
                .document(uid)
                .get()
                .await()
            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                emit(Result.success(user))
            } else {
                emit(Result.success(null))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun updateLastLoginFlow(uid: String): Flow<Result<Unit>> = flow {
        try {
            firestore.collection("users")
                .document(uid)
                .update("lastLogin", FieldValue.serverTimestamp())
                .await()
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
