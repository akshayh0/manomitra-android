package com.manomitra.app.auth

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * User
 *
 * Data model for a user profile in Firestore.
 */
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImage: String = "",
    @ServerTimestamp val createdAt: Date? = null,
    val onboardingCompleted: Boolean = true,
    val moodStreak: Int = 0,
    val journalCount: Int = 0,
    @ServerTimestamp val lastLogin: Date? = null
)
