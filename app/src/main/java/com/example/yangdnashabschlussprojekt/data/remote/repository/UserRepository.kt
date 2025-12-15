package com.example.yangdnashabschlussprojekt.data.remote.repository

import android.net.Uri
import androidx.core.net.toUri
import com.example.yangdnashabschlussprojekt.data.remote.firebaseToLocalUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository(private val firebaseAuth: FirebaseAuth) {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _userName = MutableStateFlow(firebaseAuth.currentUser?.displayName ?: "Gast")

    private val _currentUser = MutableStateFlow(firebaseAuth.currentUser?.let { firebaseToLocalUser(it) })
    val currentUser = _currentUser.asStateFlow()

    val isAuthenticated = _currentUser.map { it != null }.stateIn(
        scope = scope,
        started = SharingStarted.Eagerly,
        initialValue = firebaseAuth.currentUser != null
    )

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
                        firestore.collection("users")
                            .document(user.uid)
                            .set(userMap)
                            .await()

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .apply { profileImageUrl?.let { photoUri = it.toUri() } }
                            .build()

                        user.updateProfile(profileUpdates).await()

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
                        _userName.value = localUser.displayName
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

    suspend fun saveTextEntry(
        recognizedText: String,
        translatedText: String
    ): Result<Unit> {
        val userId = firebaseAuth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("User not logged in. Cannot save text."))

        if (recognizedText.isBlank()) {
            return Result.failure(IllegalArgumentException("Recognized text cannot be empty."))
        }

        val textData = hashMapOf(
            "recognizedText" to recognizedText,
            "translatedText" to translatedText,
            "timestamp" to System.currentTimeMillis()
        )

        return try {
            firestore.collection("users")
                .document(userId)
                .collection("texts")
                .add(textData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}