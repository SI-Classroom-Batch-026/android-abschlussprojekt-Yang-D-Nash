package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WelcomeViewModel(
    private val cameraManager: CameraManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        "SmartVision Shared ist auf ${cameraManager.platformName} bereit."
    )
    val uiState = _uiState.asStateFlow()

    fun updateText(text: String) {
        _uiState.value = text
    }

    fun onCameraButtonClick() {
        _uiState.value = cameraManager.openCamera()
    }
}
