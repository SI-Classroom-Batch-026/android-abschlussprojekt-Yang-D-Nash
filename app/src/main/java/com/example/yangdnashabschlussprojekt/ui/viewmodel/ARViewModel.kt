package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.TextUiState
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Feature
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ARViewModel(
    private val visionRepository: VisionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TextUiState())
    val uiState: StateFlow<TextUiState> = _uiState.asStateFlow()
    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes = _boundingBoxes.asStateFlow()
    private val _detectedObjectLabel = MutableStateFlow("")
    val detectedObjectLabel = _detectedObjectLabel.asStateFlow()
    private val _isCloudLoading = MutableStateFlow(false)
    val isCloudLoading = _isCloudLoading.asStateFlow()
    private val _isCloudResult = MutableStateFlow(false)
    val isCloudResult = _isCloudResult.asStateFlow()
    private val _frameSize = MutableStateFlow(Size(0, 0))
    val frameSize = _frameSize.asStateFlow()
    private var cloudLabelOverride: String? = null
    private var lastAnalyzedTimestamp = 0L
    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableClassification() // OHNE DAS GIBT ES KEINEN TEXT!
            .enableMultipleObjects() // Optional: erkennt mehrere Dinge gleichzeitig
            .build()
    )
    fun analyzeWithCloudVision(base64Image: String) {
        viewModelScope.launch {
            _isCloudLoading.value = true
            try {
                val response = visionRepository.analyzeImage(
                    base64Image,
                    listOf(
                        Feature(type = "LABEL_DETECTION", maxResults = 5),
                        Feature(type = "TEXT_DETECTION", maxResults = 1)
                    )
                )
                val firstResponse = response.responses.firstOrNull()
                val result = firstResponse?.labelAnnotations?.firstOrNull()?.description
                    ?: firstResponse?.fullTextAnnotation?.text?.take(20)
                    ?: "OBJEKT ERKANNT"
                cloudLabelOverride = result
                _detectedObjectLabel.value = result
                _isCloudResult.value = true
            } catch (e: Exception) {
                Log.e("AR_DEBUG", "Cloud Error: ${e.message}")
                _detectedObjectLabel.value = "FEHLER"
            } finally {
                _isCloudLoading.value = false
            }
        }
    }
    @OptIn(ExperimentalGetImage::class)
    fun analyzeImageProxy(imageProxy: ImageProxy) {
        val ts = System.currentTimeMillis()
        // Zeit-Check ZUERST, um CPU zu sparen
        if (ts - lastAnalyzedTimestamp < 150) {
            imageProxy.close()
            return
        }
        lastAnalyzedTimestamp = ts
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }
        val rotation = imageProxy.imageInfo.rotationDegrees
        _frameSize.value = Size(
            if (rotation % 180 != 0) imageProxy.height else imageProxy.width,
            if (rotation % 180 != 0) imageProxy.width else imageProxy.height
        )
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        objectDetector.process(image)
            .addOnSuccessListener { objects ->
                _boundingBoxes.value = objects.map { obj ->
                    TimedBoundingBox(
                        id = obj.trackingId ?: obj.hashCode(),
                        label = cloudLabelOverride ?: (obj.labels.firstOrNull()?.text ?: "SCANNE..."),
                        left = obj.boundingBox.left.toFloat(),
                        top = obj.boundingBox.top.toFloat(),
                        right = obj.boundingBox.right.toFloat(),
                        bottom = obj.boundingBox.bottom.toFloat(),
                        timestamp = if (_isCloudResult.value) Long.MAX_VALUE else ts,
                        color = if (_isCloudResult.value) Color(0xFF00FFCC) else Color(0xFF00BFFF),
                        frameWidth = _frameSize.value.width,
                        frameHeight = _frameSize.value.height
                    )
                }
            }
            .addOnFailureListener { Log.e("AR_DEBUG", "ML Kit Error: ${it.message}") }
            .addOnCompleteListener { imageProxy.close() } // ESSENTIELL
    }
    fun onLiveTextDetected(text: String) {
        _uiState.value = _uiState.value.copy(currentLiveText = text)
    }
    fun resetCloudResult() {
        cloudLabelOverride = null
        _isCloudResult.value = false
        _detectedObjectLabel.value = ""
        _boundingBoxes.value = emptyList()
    }
    override fun onCleared() {
        super.onCleared()
        objectDetector.close()
    }
}