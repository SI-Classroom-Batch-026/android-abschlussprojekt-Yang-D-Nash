package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.remote.AppUser
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WelcomeViewModel(
    private val userRepository: UserRepository,
    private val cameraManager: CameraXManager,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    val currentUser: StateFlow<AppUser?> = userRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )
    fun startOnboardingAgain() {
        settingsRepository.setOnboardingComplete(false)
    }
}