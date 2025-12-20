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
import com.example.yangdnashabschlussprojekt.data.remote.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.AnnotateImageRequest
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Feature
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Image
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.VisionApiRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ARViewModel : ViewModel(), KoinComponent {

    private val visionApiService: VisionApiService by inject()
    private val API_KEY = "DEIN_KEY_HIER_EINSETZEN"

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
            .enableClassification()
            .build()
    )

    fun analyzeWithCloudVision(base64Image: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isCloudLoading.value = true
            try {
                // Header entfernen, falls vorhanden
                val cleanBase64 = if (base64Image.contains(",")) base64Image.split(",")[1] else base64Image

                val request = VisionApiRequest(listOf(AnnotateImageRequest(
                    Image(cleanBase64),
                    listOf(Feature("LABEL_DETECTION", 5), Feature("TEXT_DETECTION", 1))
                )))

                val response = visionApiService.annotateImage(API_KEY, request)
                val firstResponse = response.responses.firstOrNull()

                // Priorität: 1. Label, 2. Text, 3. Default
                val result = firstResponse?.labelAnnotations?.firstOrNull()?.description
                    ?: firstResponse?.fullTextAnnotation?.text?.take(30)
                    ?: "IDENTIFIED OBJECT"

                Log.d("AR_DEBUG", "Cloud Response: $result")

                cloudLabelOverride = result
                _detectedObjectLabel.value = result
                _isCloudResult.value = true

            } catch (e: Exception) {
                Log.e("AR_DEBUG", "Cloud Error: ${e.message}")
                _detectedObjectLabel.value = "CONNECTION ERROR"
                _isCloudResult.value = true
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
        val w = if (rotation % 180 != 0) imageProxy.height else imageProxy.width
        val h = if (rotation % 180 != 0) imageProxy.width else imageProxy.height

        _frameSize.value = Size(w, h)

        objectDetector.process(InputImage.fromMediaImage(mediaImage, rotation))
            .addOnSuccessListener { objects ->
                val mapped = objects.map { obj ->
                    TimedBoundingBox(
                        id = obj.trackingId ?: obj.hashCode(),
                        label = cloudLabelOverride ?: (obj.labels.firstOrNull()?.text ?: "TARGETING..."),
                        left = obj.boundingBox.left.toFloat(),
                        top = obj.boundingBox.top.toFloat(),
                        right = obj.boundingBox.right.toFloat(),
                        bottom = obj.boundingBox.bottom.toFloat(),
                        timestamp = if (_isCloudResult.value) Long.MAX_VALUE else ts,
                        color = if (_isCloudResult.value) Color(0xFF00FFCC) else Color(0xFF00BFFF),
                        frameWidth = w, frameHeight = h
                    )
                }
                _boundingBoxes.value = mapped
                if (!_isCloudResult.value) {
                    _detectedObjectLabel.value = mapped.firstOrNull()?.label ?: ""
                }
            }
            .addOnCompleteListener { imageProxy.close() }
        lastAnalyzedTimestamp = ts
    }

    fun resetCloudResult() {
        cloudLabelOverride = null
        _isCloudResult.value = false
        _detectedObjectLabel.value = ""
        _boundingBoxes.value = emptyList()
    }
}