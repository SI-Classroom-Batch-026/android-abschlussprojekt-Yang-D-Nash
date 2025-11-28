package com.example.yangdnashabschlussprojekt.data.repository

import android.net.Uri
import com.example.yangdnashabschlussprojekt.data.model.AppUser
import com.example.yangdnashabschlussprojekt.data.model.firebaseToLocalUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository(private val firebaseAuth: FirebaseAuth) {

    private val _userName = MutableStateFlow(firebaseAuth.currentUser?.displayName ?: "Gast")
    val userName = _userName.asStateFlow()

    private val _currentUser = MutableStateFlow<AppUser?>(null)

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
                    val firebaseUser = firebaseAuth.currentUser
                    if (firebaseUser != null) {
                        val localUser = firebaseToLocalUser(firebaseUser)

                        _currentUser.value = localUser

                        _userName.value = localUser.name
                    }
                    onComplete(true, null)
                } else onComplete(false, task.exception?.localizedMessage)
            }
    }
    fun logout() {
        firebaseAuth.signOut()
        _userName.value = "Gast"
    }
}
