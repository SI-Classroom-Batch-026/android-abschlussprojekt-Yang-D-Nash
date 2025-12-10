package com.example.yangdnashabschlussprojekt.ui.viewmodel

sealed class CloudRecognitionState {
    object Idle : CloudRecognitionState()
    object Loading : CloudRecognitionState()
    data class Success(val recognizedText: String) : CloudRecognitionState()
    data class Error(val message: String) : CloudRecognitionState()
}