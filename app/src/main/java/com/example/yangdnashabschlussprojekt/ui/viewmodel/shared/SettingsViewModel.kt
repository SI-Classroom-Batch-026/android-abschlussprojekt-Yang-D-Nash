package com.example.yangdnashabschlussprojekt.ui.viewmodel.shared

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.remote.AppUser
import com.example.yangdnashabschlussprojekt.data.remote.RegistrationResult
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {
    val currentUser: StateFlow<AppUser?> = userRepository.currentUser
    private val _registrationResult = MutableStateFlow<RegistrationResult?>(null)
    val registrationResult: StateFlow<RegistrationResult?> = _registrationResult.asStateFlow()
    private val _authResult = MutableStateFlow<String?>(null)
    val authResult: StateFlow<String?> = _authResult.asStateFlow()
    fun resetRegistrationResult() {
        _registrationResult.value = null
    }
    fun registerUser(
        email: String,
        password: String,
        displayName: String,
        profileImageUri: Uri? = null
    ) {
        viewModelScope.launch {

            userRepository.registerUser(email, password, displayName, profileImageUri) { success, error ->
                _registrationResult.value = RegistrationResult(success, error)
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