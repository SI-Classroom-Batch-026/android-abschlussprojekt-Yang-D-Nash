package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    val userName: StateFlow<String> = userRepository.userName

    private val _registrationResult = MutableStateFlow<Pair<Boolean, String?>>(false to null)
    val registrationResult: StateFlow<Pair<Boolean, String?>> = _registrationResult

    private val _authResult = MutableStateFlow<String?>(null)
    val authResult: StateFlow<String?> = _authResult

    fun registerUser(email: String, password: String, displayName: String, profileImageUri: Uri? = null) {
        viewModelScope.launch {
            userRepository.registerUser(email, password, displayName, profileImageUri) { success, error ->
                _registrationResult.value = success to error
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.login(email, password) { success, error ->
                _authResult.value = if (success) "Erfolgreich eingeloggt" else error
            }
        }
    }

    fun logout() {
        userRepository.logout()
    }
}
