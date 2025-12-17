package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
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
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val tag = "ARViewModel"

    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()

    // Bounding Boxes für das Overlay (genauso wie im TextViewModel)
    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes: StateFlow<List<TimedBoundingBox>> = _boundingBoxes

    // Label des prominentesten Objekts (für UI-Anzeige unten/oben)
    private val _detectedObjectLabel = MutableStateFlow("")
    val detectedObjectLabel: StateFlow<String> = _detectedObjectLabel

    private val _frameSize = MutableStateFlow(Size(0, 0))
    val frameSize: StateFlow<Size> = _frameSize.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(true)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private var lastAnalyzedTimestamp = 0L
    private val frameThrottleIntervalMs = 100L // Intervall für flüssiges Tracking

    // ML Kit Object Detector Initialisierung
    // STREAM_MODE ist wichtig für Video-Feeds (nutzt Tracking IDs)
    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableClassification() // Versucht zu erkennen, WAS es ist (Kategorien)
            .enableMultipleObjects()
            .build()
    )

    override fun onCleared() {
        super.onCleared()
        objectDetector.close()
    }

    /**
     * Hauptfunktion für die Live-Kamera-Analyse
     */
    @OptIn(ExperimentalGetImage::class)
    fun analyzeImageProxy(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()

        // 1. Throttling: CPU sparen, nicht jeden Frame analysieren
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

        // 2. Rotation & Größe bestimmen
        val rotation = imageProxy.imageInfo.rotationDegrees
        val isRotated = rotation == 90 || rotation == 270
        val width = if (isRotated) imageProxy.height else imageProxy.width
        val height = if (isRotated) imageProxy.width else imageProxy.height

        if (_frameSize.value.width != width || _frameSize.value.height != height) {
            _frameSize.value = Size(width, height)
        }

        val image = InputImage.fromMediaImage(mediaImage, rotation)

        // 3. ML Kit Process
        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                processDetectedObjects(detectedObjects, width, height, currentTimestamp)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Objekterkennung fehlgeschlagen", e)
            }
            .addOnCompleteListener {
                // WICHTIG: ImageProxy immer schließen!
                imageProxy.close()
            }
    }

    /**
     * Optional: Analyse für statische Bilder (Bitmaps)
     */
    fun analyzeFrame(bitmap: Bitmap) {
        val currentTimestamp = System.currentTimeMillis()
        if (!_isAnalyzing.value) return

        if (_frameSize.value.width == 0) {
            _frameSize.value = Size(bitmap.width, bitmap.height)
        }

        val image = InputImage.fromBitmap(bitmap, 0)

        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                processDetectedObjects(detectedObjects, bitmap.width, bitmap.height, currentTimestamp)
            }
            .addOnFailureListener { e ->
                Log.e(tag, "Objekterkennung (Bitmap) fehlgeschlagen", e)
            }
    }

    /**
     * Wandelt ML Kit Objekte in unsere TimedBoundingBoxen um
     */
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

            // Labels auslesen (z.B. "Home good", "Fashion good", "Food")
            // Wenn kein Label da ist, nehmen wir "Objekt" + TrackingID
            val labelObj = obj.labels.firstOrNull()
            val labelText = if (labelObj != null) {
                "${labelObj.text} ${(labelObj.confidence * 100).toInt()}%"
            } else {
                "Objekt"
            }

            TimedBoundingBox(
                id = obj.trackingId ?: obj.hashCode(), // Tracking ID hält die Box stabil
                label = labelText,
                left = rect.left.toFloat(),
                top = rect.top.toFloat(),
                right = rect.right.toFloat(),
                bottom = rect.bottom.toFloat(),
                timestamp = timestamp,
                color = Color.Cyan, // Andere Farbe zur Unterscheidung von Text
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
        }

        _boundingBoxes.value = mappedBoxes

        // Das größte Objekt als Haupt-Label setzen
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