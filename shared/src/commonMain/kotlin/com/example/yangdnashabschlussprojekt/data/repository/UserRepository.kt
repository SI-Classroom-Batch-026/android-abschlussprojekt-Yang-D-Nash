package com.example.yangdnashabschlussprojekt.data.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {

    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val storage = Firebase.storage

    private val _currentUser = MutableStateFlow(auth.currentUser)
    val currentUser = _currentUser.asStateFlow()

    suspend fun login(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password)
            _currentUser.value = auth.currentUser
        } catch (e: Exception) {
        }
    }

    fun logout() {
        _currentUser.value = null
    }
}