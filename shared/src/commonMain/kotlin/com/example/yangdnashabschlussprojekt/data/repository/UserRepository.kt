package com.example.yangdnashabschlussprojekt.data.repository

import com.example.yangdnashabschlussprojekt.data.model.CloudHistoryDocument
import com.example.yangdnashabschlussprojekt.data.model.HistoryRecord
import com.example.yangdnashabschlussprojekt.feature.model.SharedUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _currentUser = MutableStateFlow(auth.currentUser?.toSharedUser())
    val currentUser = _currentUser.asStateFlow()

    init {
        scope.launch {
            auth.authStateChanged.collect { firebaseUser ->
                _currentUser.value = firebaseUser?.toSharedUser()
            }
        }
    }

    suspend fun login(email: String, password: String): String? {
        return runCatching {
            auth.signInWithEmailAndPassword(email, password)
            syncCurrentUser()
        }.exceptionOrNull()?.message
    }

    suspend fun register(email: String, password: String, displayName: String): String? {
        val sanitizedDisplayName = displayName.trim()
        return runCatching {
            auth.createUserWithEmailAndPassword(email.trim(), password)
            auth.currentUser?.updateProfile(
                displayName = sanitizedDisplayName.ifBlank { null }
            )
            syncCurrentUser()
        }.exceptionOrNull()?.message
    }

    suspend fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    suspend fun saveHistoryEntry(
        sourceText: String,
        translatedText: String,
        timestamp: Long
    ): Result<Unit> {
        val userId = auth.currentUser?.uid
            ?: return Result.failure(IllegalStateException("Nicht angemeldet"))

        return runCatching {
            firestore.collection("users")
                .document(userId)
                .collection("history")
                .add(
                    CloudHistoryDocument(
                        sourceText = sourceText,
                        translatedText = translatedText,
                        timestamp = timestamp
                    )
                )
            Unit
        }
    }

    suspend fun getCloudHistory(): List<HistoryRecord> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        return runCatching {
            firestore.collection("users")
                .document(userId)
                .collection("history")
                .orderBy("timestamp", Direction.DESCENDING)
                .get()
                .documents
                .mapNotNull { document ->
                    runCatching {
                        val item = document.data<CloudHistoryDocument>()
                        HistoryRecord(
                            cloudId = document.id,
                            sourceText = item.sourceText,
                            translatedText = item.translatedText,
                            timestamp = item.timestamp,
                            isFromCloud = true
                        )
                    }.getOrNull()
                }
        }.getOrDefault(emptyList())
    }

    suspend fun deleteCloudHistoryEntry(cloudId: String) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users")
            .document(userId)
            .collection("history")
            .document(cloudId)
            .delete()
    }

    suspend fun clearCloudHistory() {
        val userId = auth.currentUser?.uid ?: return
        val collection = firestore.collection("users")
            .document(userId)
            .collection("history")

        collection.get().documents.forEach { document ->
            document.reference.delete()
        }
    }

    private fun syncCurrentUser() {
        _currentUser.value = auth.currentUser?.toSharedUser()
    }
}

private fun FirebaseUser.toSharedUser(): SharedUser {
    return SharedUser(
        id = uid,
        displayName = displayName ?: "Gast",
        email = email,
        profileImageUrl = photoURL
    )
}
