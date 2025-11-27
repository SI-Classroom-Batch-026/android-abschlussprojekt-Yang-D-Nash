package com.example.yangdnashabschlussprojekt.data.repository

import com.google.firebase.auth.FirebaseAuth

class UserRepository(
    private val firebaseAuth: FirebaseAuth
) {
    fun getCurrentUser() = firebaseAuth.currentUser

    fun getUserName(): String {
        return firebaseAuth.currentUser?.displayName ?: "Gast"
    }
}
