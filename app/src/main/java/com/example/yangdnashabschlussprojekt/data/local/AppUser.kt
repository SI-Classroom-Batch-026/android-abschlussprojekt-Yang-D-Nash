package com.example.yangdnashabschlussprojekt.data.local

import com.google.firebase.auth.FirebaseUser

data class AppUser(
    val uid: String,
    val name: String,
    val email: String?,
    val profileImageUrl: String?
)

fun firebaseToLocalUser(firebaseUser: FirebaseUser): AppUser {
    return AppUser(
        uid = firebaseUser.uid,
        name = firebaseUser.displayName ?: "Gast",
        email = firebaseUser.email,
        profileImageUrl = firebaseUser.photoUrl?.toString()
    )
}
