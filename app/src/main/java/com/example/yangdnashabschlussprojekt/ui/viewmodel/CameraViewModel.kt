package com.example.yangdnashabschlussprojekt.ui.viewmodel // Der Pfad bleibt

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.TranslatedTextResult
import com.example.yangdnashabschlussprojekt.service.TranslationService
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "CameraViewModel"

    private val cameraXManager = CameraXManager(application.applicationContext)

    private val translationService = TranslationService()

    var translatedTextResult by mutableStateOf<TranslatedTextResult?>(null)
        private set

    var isTranslating by mutableStateOf(false)
        private set

    fun getCameraXManager(): CameraXManager = cameraXManager

    fun captureImage() {
        if (isTranslating) {
            Log.w(tag, "Capture already in progress, ignoring.")
            return
        }

        viewModelScope.launch {
            try {
                isTranslating = true
                translatedTextResult = null

                val base64Image = suspendCaptureImage()
                Log.d(tag, "Image successfully captured and encoded.")

                val result = translationService.translateImage(base64Image)

                translatedTextResult = result

            } catch (e: Exception) {
                Log.e(tag, "Error during capture or simulated translation: ${e.message}", e)
            } finally {
                isTranslating = false
            }
        }
    }

    suspend fun suspendCaptureImage(): String = suspendCancellableCoroutine { continuation ->

        cameraXManager.captureForCloudScan(
            onCaptured = { base64String ->
                if (continuation.isActive) continuation.resume(base64String)
            },
            onError = { exception ->
                if (continuation.isActive) continuation.resumeWithException(exception)
            }
        )
        continuation.invokeOnCancellation {
        }
    }
}