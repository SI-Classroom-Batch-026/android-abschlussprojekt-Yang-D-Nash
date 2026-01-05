package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ARViewModel(private val visionRepository: VisionRepository) : ViewModel(), ImageAnalysis.Analyzer {

    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes = _boundingBoxes.asStateFlow()

    private val _isCloudLoading = MutableStateFlow(false)
    val isCloudLoading = _isCloudLoading.asStateFlow()

    private val _isCloudResult = MutableStateFlow(false)
    val isCloudResult = _isCloudResult.asStateFlow()

    private val _detectedObjectLabel = MutableStateFlow("")
    val detectedObjectLabel = _detectedObjectLabel.asStateFlow()

    private var lastAnalyzedTimestamp = 0L
    private val FPS_LIMIT = 30L
    private var currentFrameSize = Size(1080, 1920)
    private val smoothingFactor = 0.35f

    private val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
    private val trackedObjectsMap = mutableMapOf<Int, TimedBoundingBox>()

    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
    )

    override fun analyze(image: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAnalyzedTimestamp >= FPS_LIMIT) {
            lastAnalyzedTimestamp = currentTime
            analyzeImageProxy(image)
        } else {
            image.close()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImageProxy(imageProxy: ImageProxy) {
        if (_isCloudLoading.value || _isCloudResult.value) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        val rotation = imageProxy.imageInfo.rotationDegrees

        currentFrameSize = if (rotation == 90 || rotation == 270) {
            Size(imageProxy.height, imageProxy.width)
        } else {
            Size(imageProxy.width, imageProxy.height)
        }

        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        objectDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                val newBoxes = detectedObjects.take(3).map { obj ->
                    val id = obj.trackingId ?: -1
                    val label = obj.labels.firstOrNull()?.text?.uppercase() ?: "OBJECT"
                    val rect = obj.boundingBox

                    val prev = trackedObjectsMap[id]
                    val smoothed = if (prev != null) {
                        prev.copy(
                            left = prev.left + (rect.left - prev.left) * smoothingFactor,
                            top = prev.top + (rect.top - prev.top) * smoothingFactor,
                            right = prev.right + (rect.right - prev.right) * smoothingFactor,
                            bottom = prev.bottom + (rect.bottom - prev.bottom) * smoothingFactor,
                            label = label,
                            frameWidth = currentFrameSize.width,
                            frameHeight = currentFrameSize.height
                        )
                    } else {
                        if (id != -1) try { toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 80) } catch(_: Exception) {}
                        TimedBoundingBox(
                            id, label,
                            rect.left.toFloat(), rect.top.toFloat(),
                            rect.right.toFloat(), rect.bottom.toFloat(),
                            System.currentTimeMillis(), Color(0xFF00E5FF),
                            currentFrameSize.width, currentFrameSize.height
                        )
                    }
                    trackedObjectsMap[id] = smoothed
                    smoothed
                }

                trackedObjectsMap.keys.retainAll(newBoxes.map { it.id }.toSet())
                _boundingBoxes.value = newBoxes
                _detectedObjectLabel.value = newBoxes.firstOrNull()?.label ?: ""
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    fun analyzeWithCloudVision(base64Image: String) {
        val lastPos = _boundingBoxes.value.firstOrNull()
        viewModelScope.launch {
            _isCloudLoading.value = true
            try {
                val response = visionRepository.analyzeImage(base64Image, listOf(Feature("LABEL_DETECTION", 1)))
                val res = response.responses.firstOrNull()?.labelAnnotations?.firstOrNull()?.description?.uppercase() ?: "OBJECT"
                _detectedObjectLabel.value = res
                _isCloudResult.value = true

                _boundingBoxes.value = listOf(
                    lastPos?.copy(label = res, color = Color(0xFF00FFCC))
                        ?: TimedBoundingBox(999, res, 0.3f, 0.3f, 0.7f, 0.7f, System.currentTimeMillis(), Color(0xFF00FFCC), 1000, 1000)
                )
            } catch (e: Exception) {
                Log.e("ARVM", "${e.message}")
            } finally {
                _isCloudLoading.value = false
            }
        }
    }

    fun resetCloudResult() {
        _isCloudResult.value = false
        _isCloudLoading.value = false
        _boundingBoxes.value = emptyList()
        _detectedObjectLabel.value = ""
        trackedObjectsMap.clear()
    }

    override fun onCleared() {
        super.onCleared()
        objectDetector.close()
        toneGen.release()
    }
}