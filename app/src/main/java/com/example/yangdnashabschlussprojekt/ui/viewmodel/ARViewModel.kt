package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class ARViewModel(
) : ViewModel() {
    private val tag = "ARViewModel"
    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes: StateFlow<List<TimedBoundingBox>> = _boundingBoxes
    private val _detectedObjectLabel = MutableStateFlow("")
    val detectedObjectLabel: StateFlow<String> = _detectedObjectLabel
    private val _frameSize = MutableStateFlow(Size(0, 0))
    val frameSize: StateFlow<Size> = _frameSize.asStateFlow()
    private val _isAnalyzing = MutableStateFlow(true)
    private var lastAnalyzedTimestamp = 0L
    private val frameThrottleIntervalMs = 100L
    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableClassification()
            .enableMultipleObjects()
            .build()
    )
    override fun onCleared() {
        super.onCleared()
        objectDetector.close()
    }
    @OptIn(ExperimentalGetImage::class)
    fun analyzeImageProxy(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (!_isAnalyzing.value || currentTimestamp - lastAnalyzedTimestamp < frameThrottleIntervalMs) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }
        lastAnalyzedTimestamp = currentTimestamp
        val rotation = imageProxy.imageInfo.rotationDegrees
        val isRotated = rotation == 90 || rotation == 270
        val width = if (isRotated) imageProxy.height else imageProxy.width
        val height = if (isRotated) imageProxy.width else imageProxy.height
        if (_frameSize.value.width != width || _frameSize.value.height != height) {
            _frameSize.value = Size(width, height)
        }
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                processDetectedObjects(detectedObjects, width, height, currentTimestamp)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Objekterkennung fehlgeschlagen", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
    private fun processDetectedObjects(
        objects: List<DetectedObject>,
        frameWidth: Int,
        frameHeight: Int,
        timestamp: Long
    ) {
        if (objects.isEmpty()) {
            _boundingBoxes.value = emptyList()
            _detectedObjectLabel.value = ""
            return
        }

        val mappedBoxes = objects.map { obj ->
            val rect: Rect = obj.boundingBox

            val labelObj = obj.labels.firstOrNull()
            val labelText = if (labelObj != null) {
                "${labelObj.text} ${(labelObj.confidence * 100).toInt()}%"
            } else {
                "Objekt"
            }
            TimedBoundingBox(
                id = obj.trackingId ?: obj.hashCode(),
                label = labelText,
                left = rect.left.toFloat(),
                top = rect.top.toFloat(),
                right = rect.right.toFloat(),
                bottom = rect.bottom.toFloat(),
                timestamp = timestamp,
                color = Color.Cyan,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
        }
        _boundingBoxes.value = mappedBoxes
        mappedBoxes.maxByOrNull {
            (it.right - it.left) * (it.bottom - it.top)
        }?.let {
            _detectedObjectLabel.value = it.label
        }
    }
    fun stopAnalysis() {
        _isAnalyzing.value = false
    }
    fun continueAnalysis() {
        _isAnalyzing.value = true
        _detectedObjectLabel.value = ""
        _boundingBoxes.value = emptyList()
    }
}