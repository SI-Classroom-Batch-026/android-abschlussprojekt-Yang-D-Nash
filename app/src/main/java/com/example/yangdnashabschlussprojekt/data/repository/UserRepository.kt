package com.example.yangdnashabschlussprojekt.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(private val firebaseAuth: FirebaseAuth) {

    private val _userName = MutableStateFlow(firebaseAuth.currentUser?.displayName ?: "Gast")
    val userName = _userName.asStateFlow()

    fun registerUser(
        email: String,
        password: String,
        displayName: String,
        profileImageUri: Uri? = null,
        onComplete: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .apply { profileImageUri?.let { photoUri = it } }
                        .build()
                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            _userName.value = displayName
                            onComplete(true, null)
                        } else onComplete(false, updateTask.exception?.localizedMessage)
                    }
                } else onComplete(false, task.exception?.localizedMessage)
            }
    }

    fun login(
        email: String,
        password: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userName.value = firebaseAuth.currentUser?.displayName ?: "Gast"
                    onComplete(true, null)
                } else onComplete(false, task.exception?.localizedMessage)
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _userName.value = "Gast"
    }
}
