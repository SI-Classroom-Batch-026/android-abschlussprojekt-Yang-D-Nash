package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.companion.DesktopCompanionClient
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Feature
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.feature.model.CompanionMode
import com.example.yangdnashabschlussprojekt.feature.model.CompanionSnapshot
import com.example.yangdnashabschlussprojekt.util.notification.TranslatorUtil
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class ARViewModel(
    private val visionRepository: VisionRepository,
    private val desktopCompanionClient: DesktopCompanionClient,
    application: Application
) : AndroidViewModel(application), ImageAnalysis.Analyzer {

    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes = _boundingBoxes.asStateFlow()

    private val _isCloudLoading = MutableStateFlow(false)
    val isCloudLoading = _isCloudLoading.asStateFlow()

    private val _isCloudResult = MutableStateFlow(false)
    val isCloudResult = _isCloudResult.asStateFlow()

    private val _detectedObjectLabel = MutableStateFlow("")
    val detectedObjectLabel = _detectedObjectLabel.asStateFlow()

    private val _translationStatus = MutableStateFlow("")
    val translationStatus = _translationStatus.asStateFlow()

    private var currentFrameSize = Size(1080, 1920)
    private val smoothingFactor = 0.35f
    private val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
    private val trackedObjectsMap = mutableMapOf<Int, TimedBoundingBox>()

    private var lastEnglishLabel = ""
    private var lastSceneCandidates = emptyList<String>()

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
                    val rawLabel = obj.labels.firstOrNull()?.text ?: "SCANNING..."

                    // Trigger Dynamische Übersetzung
                    if (rawLabel != "SCANNING..." && rawLabel != lastEnglishLabel) {
                        translateLiveResult(rawLabel)
                    }

                    val rect = obj.boundingBox
                    val prev = trackedObjectsMap[id]
                    val box = if (prev != null) {
                        prev.copy(
                            left = prev.left + (rect.left - prev.left) * smoothingFactor,
                            top = prev.top + (rect.top - prev.top) * smoothingFactor,
                            right = prev.right + (rect.right - prev.right) * smoothingFactor,
                            bottom = prev.bottom + (rect.bottom - prev.bottom) * smoothingFactor,
                            // Nutze das übersetzte Label, falls vorhanden
                            label = if (_detectedObjectLabel.value.isNotEmpty() && rawLabel == lastEnglishLabel) {
                                _detectedObjectLabel.value
                            } else {
                                rawLabel.uppercase()
                            }
                        )
                    } else {
                        if (id != -1) try { toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 80) } catch(_: Exception) {}
                        TimedBoundingBox(id, rawLabel.uppercase(), rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat(), System.currentTimeMillis(), Color(0xFF00E5FF), currentFrameSize.width, currentFrameSize.height)
                    }
                    trackedObjectsMap[id] = box
                    box
                }
                trackedObjectsMap.keys.retainAll(newBoxes.map { it.id }.toSet())
                _boundingBoxes.value = newBoxes
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun translateLiveResult(englishText: String) {
        lastEnglishLabel = englishText
        val deviceLang = Locale.getDefault().language

        TranslatorUtil.translateDynamic(
            context = getApplication(),
            sourceText = englishText,
            sourceLang = "en",
            targetLang = deviceLang,
            onStatusUpdate = { status -> _translationStatus.value = status },
            onResult = { translated ->
                val upperResult = translated.uppercase()
                _detectedObjectLabel.value = upperResult
                _translationStatus.value = ""
                publishArSnapshot(recognizedObject = upperResult, statusMessage = "AR-Ergebnis live gespiegelt.")

                // Live-Update der aktuellen Boxen in der Liste
                _boundingBoxes.value = _boundingBoxes.value.map { box ->
                    if (box.label.equals(englishText, ignoreCase = true)) {
                        box.copy(label = upperResult)
                    } else box
                }
            }
        )
    }

    fun analyzeWithCloudVision(base64Image: String) {
        viewModelScope.launch {
            _isCloudLoading.value = true
            try {
                val response = visionRepository.analyzeImage(base64Image, listOf(
                    Feature("LABEL_DETECTION", 5),
                    Feature("OBJECT_LOCALIZATION", 5),
                    Feature("LOGO_DETECTION", 2)
                ))

                val firstRes = response.responses.firstOrNull()
                val logoText = firstRes?.logoAnnotations?.firstOrNull()?.description
                val cloudObjects = firstRes?.localizedObjectAnnotations
                val labelText = firstRes?.labelAnnotations?.firstOrNull()?.description

                val finalEnglishLabel = (logoText ?: cloudObjects?.firstOrNull()?.name ?: labelText ?: "OBJECT")

                translateLiveResult(finalEnglishLabel)
                lastSceneCandidates = buildList {
                    logoText?.let(::add)
                    cloudObjects?.mapTo(this) { it.name }
                    labelText?.let(::add)
                }.filter { it.isNotBlank() }.distinct()
                publishArSnapshot(
                    recognizedObject = finalEnglishLabel,
                    statusMessage = "Cloud-Objekterkennung abgeschlossen."
                )
                _isCloudResult.value = true

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
    override fun onCleared() {
        super.onCleared()
        objectDetector.close()
    }
    fun resetCloudResult() {
        _isCloudResult.value = false
        _isCloudLoading.value = false
        _detectedObjectLabel.value = ""
        _translationStatus.value = ""
        lastEnglishLabel = ""
        lastSceneCandidates = emptyList()
        trackedObjectsMap.clear()
        _boundingBoxes.value = emptyList()
        publishArSnapshot(recognizedObject = null, statusMessage = "AR-Ansicht wurde zurueckgesetzt.")
    }

    private fun publishArSnapshot(
        recognizedObject: String?,
        statusMessage: String
    ) {
        viewModelScope.launch {
            desktopCompanionClient.publishSnapshot(
                CompanionSnapshot(
                    deviceName = Build.MODEL ?: "Android",
                    sourcePlatform = "Android",
                    activeMode = CompanionMode.AR,
                    updatedAtEpochMillis = System.currentTimeMillis(),
                    statusMessage = statusMessage,
                    recognizedObject = recognizedObject,
                    objectCandidates = lastSceneCandidates
                )
            )
        }
    }
}
