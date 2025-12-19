package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ARViewModel : ViewModel() {
    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes: StateFlow<List<TimedBoundingBox>> = _boundingBoxes.asStateFlow()

    private val _detectedObjectLabel = MutableStateFlow("")
    val detectedObjectLabel: StateFlow<String> = _detectedObjectLabel.asStateFlow()

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
            .enableClassification()
            .build()
    )

    fun analyzeWithCloudVision(base64Image: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isCloudLoading.value = true
            _isCloudResult.value = false
            try {
                delay(2000) // Simulation
                val result = "Sony Alpha 7 IV"

                cloudLabelOverride = result
                _detectedObjectLabel.value = result
                _isCloudResult.value = true
            } catch (e: Exception) {
                Log.e("ARViewModel", "Cloud Error: ${e.message}")
            } finally {
                _isCloudLoading.value = false
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    fun analyzeImageProxy(imageProxy: ImageProxy) {
        val ts = System.currentTimeMillis()
        if (ts - lastAnalyzedTimestamp < 150) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: return imageProxy.close()
        val rotation = imageProxy.imageInfo.rotationDegrees
        val width = if (rotation == 90 || rotation == 270) imageProxy.height else imageProxy.width
        val height = if (rotation == 90 || rotation == 270) imageProxy.width else imageProxy.height

        if (_frameSize.value.width != width) _frameSize.value = Size(width, height)

        objectDetector.process(InputImage.fromMediaImage(mediaImage, rotation))
            .addOnSuccessListener { objects ->
                processDetectedObjects(objects, width, height, ts)
            }
            .addOnCompleteListener { imageProxy.close() }
        lastAnalyzedTimestamp = ts
    }

    private fun processDetectedObjects(objects: List<DetectedObject>, w: Int, h: Int, ts: Long) {
        // Wenn keine Objekte gefunden werden:
        if (objects.isEmpty()) {
            // Nur löschen, wenn wir NICHT im Cloud-Modus sind
            if (!_isCloudResult.value) {
                _boundingBoxes.value = emptyList()
                _detectedObjectLabel.value = ""
            }
            return
        }

        // Mappe die gefundenen Objekte auf unsere Boxen
        val mapped = objects.map { obj ->
            TimedBoundingBox(
                id = obj.trackingId ?: obj.hashCode(),
                // WICHTIG: Wenn Cloud-Ergebnis da, nutze IMMER das Cloud-Label
                label = cloudLabelOverride ?: (obj.labels.firstOrNull()?.text ?: "Objekt"),
                left = obj.boundingBox.left.toFloat(),
                top = obj.boundingBox.top.toFloat(),
                right = obj.boundingBox.right.toFloat(),
                bottom = obj.boundingBox.bottom.toFloat(),
                timestamp = ts,
                color = if (_isCloudResult.value) Color.Green else Color.Cyan,
                frameWidth = w,
                frameHeight = h
            )
        }

        _boundingBoxes.value = mapped
        if (!_isCloudResult.value) {
            _detectedObjectLabel.value = mapped.firstOrNull()?.label ?: ""
        }
    }

    fun resetCloudResult() {
        cloudLabelOverride = null
        _isCloudResult.value = false
        _detectedObjectLabel.value = ""
        _boundingBoxes.value = emptyList()
    }
}