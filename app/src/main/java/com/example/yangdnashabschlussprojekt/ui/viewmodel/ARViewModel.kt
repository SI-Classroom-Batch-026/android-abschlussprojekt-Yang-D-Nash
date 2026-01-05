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
        if (_isCloudLoading.value || _isCloudResult.value) {
            image.close()
            return
        }
        analyzeImageProxy(image)
    }

    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        val rotation = imageProxy.imageInfo.rotationDegrees
        currentFrameSize = if (rotation == 90 || rotation == 270) Size(imageProxy.height, imageProxy.width) else Size(imageProxy.width, imageProxy.height)

        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        objectDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                val newBoxes = detectedObjects.map { obj ->
                    val id = obj.trackingId ?: -1
                    val label = obj.labels.firstOrNull()?.text?.uppercase() ?: "SCANNING..."
                    val rect = obj.boundingBox

                    val prev = trackedObjectsMap[id]
                    val box = if (prev != null) {
                        prev.copy(
                            left = prev.left + (rect.left - prev.left) * smoothingFactor,
                            top = prev.top + (rect.top - prev.top) * smoothingFactor,
                            right = prev.right + (rect.right - prev.right) * smoothingFactor,
                            bottom = prev.bottom + (rect.bottom - prev.bottom) * smoothingFactor,
                            label = label
                        )
                    } else {
                        if (id != -1) try { toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 80) } catch(_: Exception) {}
                        TimedBoundingBox(id, label, rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), System.currentTimeMillis(), Color(0xFF00E5FF), currentFrameSize.width, currentFrameSize.height)
                    }
                    trackedObjectsMap[id] = box
                    box
                }
                trackedObjectsMap.keys.retainAll(newBoxes.map { it.id }.toSet())
                _boundingBoxes.value = newBoxes
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    // --- CLOUD SCAN MIT OBJEKT-LOKALISIERUNG ---
    fun analyzeWithCloudVision(base64Image: String) {
        viewModelScope.launch {
            _isCloudLoading.value = true
            try {
                // Wir fragen Labels UND Objekt-Positionen ab!
                val response = visionRepository.analyzeImage(base64Image, listOf(
                    Feature("LABEL_DETECTION", 5),
                    Feature("OBJECT_LOCALIZATION", 5),
                    Feature("LOGO_DETECTION", 2)
                ))

                val firstRes = response.responses.firstOrNull()

                // 1. Priorität: Logos (z.B. Coca Cola, Apple)
                // 2. Priorität: Lokalisierte Objekte (mit Boxen)
                // 3. Priorität: Allgemeine Labels
                val logoText = firstRes?.logoAnnotations?.firstOrNull()?.description
                val cloudObjects = firstRes?.localizedObjectAnnotations
                val labelText = firstRes?.labelAnnotations?.firstOrNull()?.description

                val finalLabel = (logoText ?: cloudObjects?.firstOrNull()?.name ?: labelText ?: "OBJECT").uppercase()

                _detectedObjectLabel.value = finalLabel
                _isCloudResult.value = true

                // Boxen aus der Cloud übernehmen, damit man sie sieht!
                val cloudBoxes = cloudObjects?.map { obj ->
                    val poly = obj.boundingPoly.normalizedVertices
                    TimedBoundingBox(
                        id = obj.hashCode(),
                        label = obj.name.uppercase(),
                        left = (poly.getOrNull(0)?.x ?: 0f) * currentFrameSize.width,
                        top = (poly.getOrNull(0)?.y ?: 0f) * currentFrameSize.height,
                        right = (poly.getOrNull(2)?.x ?: 0.5f) * currentFrameSize.width,
                        bottom = (poly.getOrNull(2)?.y ?: 0.5f) * currentFrameSize.height,
                        timestamp = System.currentTimeMillis(),
                        color = Color(0xFF00FFCC),
                        frameWidth = currentFrameSize.width,
                        frameHeight = currentFrameSize.height
                    )
                } ?: emptyList()

                _boundingBoxes.value = cloudBoxes.ifEmpty { _boundingBoxes.value }

            } catch (e: Exception) {
                Log.e("ARVM", "Error: ${e.message}")
            } finally {
                _isCloudLoading.value = false
            }
        }
    }

    fun resetCloudResult() {
        _isCloudResult.value = false
        _isCloudLoading.value = false
        _detectedObjectLabel.value = ""
        trackedObjectsMap.clear()
        _boundingBoxes.value = emptyList()
    }
}