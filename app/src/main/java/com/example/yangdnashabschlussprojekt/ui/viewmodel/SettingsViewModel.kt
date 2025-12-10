package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.remote.AppUser
import com.example.yangdnashabschlussprojekt.data.remote.RegistrationResult
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val userRepository: UserRepository) : ViewModel() {

    val currentUser: StateFlow<AppUser?> = userRepository.currentUser

    private val _registrationResult = mutableStateOf<RegistrationResult?>(null)
    val registrationResult: State<RegistrationResult?> = _registrationResult

    private val _authResult = MutableStateFlow<String?>(null)

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
