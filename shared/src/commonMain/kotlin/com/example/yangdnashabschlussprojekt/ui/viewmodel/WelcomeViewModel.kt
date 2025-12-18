package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WelcomeViewModel(
    private val userRepository: UserRepository,
    private val cameraManager: CameraManager
) : ViewModel() {

    private val _uiState = MutableStateFlow("Bereit")
    val uiState = _uiState.asStateFlow()

    fun updateText(text: String) { _uiState.value = text }

    fun onCameraButtonClick() {
        cameraManager.openCamera()
    }
}