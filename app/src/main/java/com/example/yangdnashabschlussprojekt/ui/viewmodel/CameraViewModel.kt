package com.example.yangdnashabschlussprojekt.ui.viewmodel // Passe den Pfad an

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.TranslatedTextResult
import com.example.yangdnashabschlussprojekt.service.TranslationService // Dein Service
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager // Dein Manager
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val cameraXManager = CameraXManager(application.applicationContext)

    private val translationService = TranslationService()

    var translatedTextResult by mutableStateOf<TranslatedTextResult?>(null)
        private set

    var isTranslating by mutableStateOf(false)
        private set

    fun getCameraXManager(): CameraXManager = cameraXManager

    fun captureImage() {
        if (isTranslating) return

        viewModelScope.launch {
            try {
                isTranslating = true
                translatedTextResult = null

                val base64Image = suspendCaptureImage()
                Log.d("CameraViewModel", "Image captured, Base64 length: ${base64Image.length}")

                val result = translationService.translateImage(base64Image)

                translatedTextResult = result

            } catch (e: Exception) {
                Log.e("CameraViewModel", "Error during capture or translation: ${e.message}")
            } finally {
                isTranslating = false
            }
        }
    }

    private suspend fun suspendCaptureImage(): String = suspendCancellableCoroutine { continuation ->

        cameraXManager.captureForCloudScan(
            onCaptured = { base64String ->
                continuation.resume(base64String)
            },
            onError = { exception ->
                continuation.resumeWithException(exception)
            }
        )

        continuation.invokeOnCancellation {
        }
    }
}