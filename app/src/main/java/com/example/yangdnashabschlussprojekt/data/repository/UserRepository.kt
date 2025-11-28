package com.example.yangdnashabschlussprojekt.data.repository

import android.net.Uri
import androidx.core.net.toUri
import com.example.yangdnashabschlussprojekt.data.local.AppUser
import com.example.yangdnashabschlussprojekt.data.local.firebaseToLocalUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository(private val firebaseAuth: FirebaseAuth) {

    private val _userName = MutableStateFlow(firebaseAuth.currentUser?.displayName ?: "Gast")
    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun registerUser(
        email: String,
        password: String,
        displayName: String,
        profileImageUri: Uri? = null,
        onComplete: (Boolean, String?) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    onComplete(false, task.exception?.localizedMessage)
                    return@addOnCompleteListener
                }

                val user = firebaseAuth.currentUser
                if (user == null) {
                    onComplete(false, "Firebase User ist null")
                    return@addOnCompleteListener
                }

                scope.launch {
                    // Profilbild hochladen, falls vorhanden
                    var profileImageUrl: String? = null
                    profileImageUri?.let { uri ->
                        profileImageUrl = try {
                            val ref = storage.reference.child("profileImages/${user.uid}/profile.jpg")
                            ref.putFile(uri).await()
                            ref.downloadUrl.await().toString()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }

                    val userMap = hashMapOf(
                        "displayName" to displayName,
                        "email" to email,
                        "profileImageUrl" to profileImageUrl
                    )

                    try {
                        // Firestore-Dokument erstellen
                        firestore.collection("users")
                            .document(user.uid)
                            .set(userMap)
                            .await()

                        // Firebase Auth Profil aktualisieren
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .apply { profileImageUrl?.let { photoUri = it.toUri() } }
                            .build()

                        user.updateProfile(profileUpdates).await()

                        // Lokales State aktualisieren
                        val localUser = firebaseToLocalUser(user)
                        _currentUser.value = localUser
                        _userName.value = displayName

                        onComplete(true, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onComplete(false, e.localizedMessage)
                    }
                }
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
                    firebaseUser?.let {
                        val localUser = firebaseToLocalUser(it)
                        _currentUser.value = localUser
                        _userName.value = localUser.name
                    }
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.localizedMessage)
                }
            }
    }

    fun logout() {
        firebaseAuth.signOut()
        _userName.value = "Gast"
        _currentUser.value = null
    }
}
