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

    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes: StateFlow<List<TimedBoundingBox>> = _boundingBoxes.asStateFlow()

    private val _isCloudLoading = MutableStateFlow(false)
    val isCloudLoading = _isCloudLoading.asStateFlow()

    private val _isCloudResult = MutableStateFlow(false)
    val isCloudResult = _isCloudResult.asStateFlow()

    private val _detectedObjectLabel = MutableStateFlow("")
    val detectedObjectLabel = _detectedObjectLabel.asStateFlow()

    private var currentFrameSize = Size(1080, 1920)
    private var snapshotBoxes: List<TimedBoundingBox> = emptyList()

    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .build()
    )

    fun analyzeWithCloudVision(base64Image: String) {
        // Snapshot der aktuellen Live-Boxen speichern
        snapshotBoxes = _boundingBoxes.value

        viewModelScope.launch {
            _isCloudLoading.value = true
            try {
                val response = visionRepository.analyzeImage(
                    base64Image,
                    listOf(Feature(type = "LABEL_DETECTION", maxResults = 1))
                )
                val result = response.responses.firstOrNull()?.labelAnnotations?.firstOrNull()?.description ?: "OBJEKT"

                _detectedObjectLabel.value = result

                // Boxen auf Cloud-Ergebnis umstellen
                _boundingBoxes.value = if (snapshotBoxes.isNotEmpty()) {
                    snapshotBoxes.map {
                        it.copy(label = result, color = Color(0xFF00FFCC), timestamp = System.currentTimeMillis())
                    }
                } else {
                    listOf(TimedBoundingBox(999, result, 0.2f, 0.3f, 0.8f, 0.7f, System.currentTimeMillis(), Color(0xFF00FFCC), 1000, 1000))
                }
                _isCloudResult.value = true
            } catch (e: Exception) {
                Log.e("VISION_DEBUG", "Cloud Error: ${e.message}")
            } finally {
                _isCloudLoading.value = false
            }
        }
    }

    fun refreshCloudBoxes() {
        if (_isCloudResult.value) {
            _boundingBoxes.value = _boundingBoxes.value.map { it.copy(timestamp = System.currentTimeMillis()) }
        }
    }

    fun resetCloudResult() {
        _isCloudResult.value = false
        _isCloudLoading.value = false
        _detectedObjectLabel.value = ""
        _boundingBoxes.value = emptyList() // WICHTIG: Komplett leer machen für Neustart
        snapshotBoxes = emptyList()
    }

    @OptIn(ExperimentalGetImage::class)
    fun analyzeImageProxy(imageProxy: ImageProxy) {
        if (_isCloudLoading.value || _isCloudResult.value) {
            imageProxy.close()
            return
        }

        val mediaImage = try { imageProxy.image } catch (e: Exception) { null } ?: return
        val rotation = imageProxy.imageInfo.rotationDegrees
        currentFrameSize = Size(
            if (rotation % 180 != 0) imageProxy.height else imageProxy.width,
            if (rotation % 180 != 0) imageProxy.width else imageProxy.height
        )

        val image = InputImage.fromMediaImage(mediaImage, rotation)
        objectDetector.process(image)
            .addOnSuccessListener { objects ->
                if (!_isCloudLoading.value && !_isCloudResult.value) {
                    _boundingBoxes.value = objects.map { obj ->
                        TimedBoundingBox(
                            id = obj.trackingId ?: obj.hashCode(),
                            label = "LIVE...",
                            left = obj.boundingBox.left.toFloat(),
                            top = obj.boundingBox.top.toFloat(),
                            right = obj.boundingBox.right.toFloat(),
                            bottom = obj.boundingBox.bottom.toFloat(),
                            timestamp = System.currentTimeMillis(),
                            color = Color(0xFF00BFFF),
                            frameWidth = currentFrameSize.width,
                            frameHeight = currentFrameSize.height
                        )
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
}