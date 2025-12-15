package com.example.yangdnashabschlussprojekt.data.remote

import com.google.firebase.auth.FirebaseUser

data class AppUser(
    val uid: String,
    val displayName: String,
    val email: String?,
    val profileImageUrl: String?
)

fun firebaseToLocalUser(firebaseUser: FirebaseUser): AppUser {
    return AppUser(
        uid = firebaseUser.uid,
        displayName = firebaseUser.displayName ?: "Gast",
        email = firebaseUser.email,
        profileImageUrl = firebaseUser.photoUrl?.toString()
    )
}