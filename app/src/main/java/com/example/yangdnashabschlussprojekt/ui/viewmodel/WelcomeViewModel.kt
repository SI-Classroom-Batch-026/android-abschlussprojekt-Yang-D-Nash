package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.remote.AppUser
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository // Check this path!
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager // Check this path!
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WelcomeViewModel(
    private val userRepository: UserRepository,
    private val cameraManager: CameraManager,
    private val settingsRepository: SettingsRepository,
    onNavigateToOnboarding: () -> Unit
) : ViewModel() {
    fun startOnboardingAgain() {
        settingsRepository.setOnboardingComplete(false)
    }
    val currentUser: StateFlow<AppUser?> = userRepository.currentUser
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        ) as StateFlow<AppUser?>
}