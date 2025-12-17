package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
// ✅ WICHTIG: Import aus dem Shared-Pfad (ohne .local)
import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
// ✅ WICHTIG: Das Shared-Modell nutzen
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class CloudRecognitionState {
    object Idle : CloudRecognitionState()
    object Loading : CloudRecognitionState()
    data class Success(val text: String) : CloudRecognitionState()
    data class Error(val message: String) : CloudRecognitionState()
}

class TextViewModel(
    private val visionRepository: VisionRepository,
    private val historyRepository: HistoryRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val tag = "TextViewModel"
    private val _uiEvent = Channel<String>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes: StateFlow<List<TimedBoundingBox>> = _boundingBoxes
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText
    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText
    private val _cloudRecognitionState = MutableStateFlow<CloudRecognitionState>(CloudRecognitionState.Idle)
    val cloudRecognitionState: StateFlow<CloudRecognitionState> = _cloudRecognitionState.asStateFlow()
    private val _frameSize = MutableStateFlow(Size(0, 0))
    val frameSize: StateFlow<Size> = _frameSize.asStateFlow()
    private val _isAnalyzing = MutableStateFlow(true)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()
    private var lastAnalyzedTimestamp = 0L
    private val frameThrottleIntervalMs = 100L
    private val recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun saveCurrentTextToHistory() {
        val recognized = _recognizedText.value
        val translated = _translatedText.value

        if (recognized.isBlank() || translated.isBlank()) {
            viewModelScope.launch {
                _uiEvent.send("Kein Text zum lokalen Speichern vorhanden.")
            }
            return
        }

        viewModelScope.launch {
            // ✅ Wir erstellen das neutrale TextHistory Objekt
            val historyEntry = TextHistory(
                id = null,
                sourceText = recognized,
                translatedText = translated,
                timestamp = System.currentTimeMillis()
            )

            // ✅ Das Shared-Repo akzeptiert nun TextHistory
            historyRepository.saveEntry(historyEntry)
            _uiEvent.send("Text lokal im Verlauf gespeichert.")
        }
    }

    private val translator by lazy {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()
        Translation.getClient(options)
    }

    override fun onCleared() {
        super.onCleared()
        translator.close()
    }

    private fun sortAndStructureText(blocks: List<Text.TextBlock>): String {
        if (blocks.isEmpty()) return ""
        val allLines = blocks.flatMap { it.lines }
        val sortedLines = allLines.sortedBy { it.boundingBox?.top ?: 0 }
        return sortedLines.joinToString("\n") { line -> line.text }
    }

    private fun updateBoundingBoxes(textBlocks: List<Text.TextBlock>, frameWidth: Int, frameHeight: Int, timestamp: Long) {
        val boxes = textBlocks.map { block ->
            val rect: Rect = block.boundingBox ?: Rect(0, 0, 0, 0)
            TimedBoundingBox(
                id = block.hashCode().toLong().toInt(),
                label = block.text,
                left = rect.left.toFloat(),
                top = rect.top.toFloat(),
                right = rect.right.toFloat(),
                bottom = rect.bottom.toFloat(),
                timestamp = timestamp,
                color = Color.Magenta,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
        }
        _boundingBoxes.value = boxes
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
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                if (visionText.textBlocks.isNotEmpty()) {
                    val structuredText = sortAndStructureText(visionText.textBlocks)
                    if (structuredText.isNotBlank()) {
                        _recognizedText.value = structuredText
                        _isAnalyzing.value = false
                        updateBoundingBoxes(visionText.textBlocks, width, height, currentTimestamp)
                        translateTextAsync(structuredText)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(tag, "ML Kit Text-Erkennung fehlgeschlagen", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun continueAnalysis() {
        _isAnalyzing.value = true
        _recognizedText.value = ""
        _translatedText.value = ""
        _boundingBoxes.value = emptyList()
        _cloudRecognitionState.value = CloudRecognitionState.Idle
    }

    fun loadFromHistory(recognized: String, translated: String) {
        _isAnalyzing.value = false
        _recognizedText.value = recognized
        _translatedText.value = translated
        _cloudRecognitionState.value = CloudRecognitionState.Success(recognized)
        _boundingBoxes.value = emptyList()
    }

    fun recognizeText(text: String) {
        if (text.isNotBlank()) {
            _isAnalyzing.value = false
            _recognizedText.value = text
            _boundingBoxes.value = emptyList()
            translateTextAsync(text)
            _cloudRecognitionState.value = CloudRecognitionState.Success(text)
        }
    }

    fun recognizeTextViaCloud(base64Image: String) {
        _isAnalyzing.value = false
        if (_cloudRecognitionState.value is CloudRecognitionState.Loading) return
        _recognizedText.value = ""
        _translatedText.value = ""
        viewModelScope.launch {
            _cloudRecognitionState.value = CloudRecognitionState.Loading
            try {
                val response = visionRepository.detectText(base64Image)
                val cloudText = response.responses.firstOrNull()
                    ?.fullTextAnnotation
                    ?.text
                    ?: throw IllegalStateException("Kein Text von der Cloud Vision API gefunden.")
                _recognizedText.value = cloudText
                _cloudRecognitionState.value = CloudRecognitionState.Success(cloudText)
                translateTextSuspend(cloudText)
            } catch (e: Exception) {
                Log.e(tag, "Cloud-Erkennung fehlgeschlagen", e)
                val errorMessage = "Cloud-Erkennung fehlgeschlagen: ${e.localizedMessage ?: e.message}"
                _cloudRecognitionState.value = CloudRecognitionState.Error(errorMessage)
                _recognizedText.value = errorMessage
            }
        }
    }

    private suspend fun translateTextSuspend(text: String) {
        try {
            translator.downloadModelIfNeeded().await()
            val translated = translator.translate(text).await()
            _translatedText.value = translated
        } catch (e: Exception) {
            Log.e(tag, "Übersetzung fehlgeschlagen", e)
            _translatedText.value = "Übersetzung fehlgeschlagen: ${e.message}"
        }
    }

    private fun translateTextAsync(text: String) {
        viewModelScope.launch {
            translateTextSuspend(text)
        }
    }

    fun saveTextToCloud() {
        val recognized = _recognizedText.value
        val translated = _translatedText.value

        if (!userRepository.isAuthenticated.value) {
            viewModelScope.launch {
                Log.e(tag, "SNACKBAR EVENT: Nicht authentifiziert.")
                _uiEvent.send("Speichern fehlgeschlagen: Sie sind nicht authentifiziert.")
            }
            return
        }
        if (recognized.isBlank() || translated.isBlank()) {
            viewModelScope.launch {
                Log.e(tag, "SNACKBAR EVENT: Kein Text.")
                _uiEvent.send("Kein Text zum Speichern vorhanden.")
            }
            return
        }
        viewModelScope.launch {
            userRepository.saveTextEntry(recognized, translated)
                .onSuccess {
                    Log.d(tag, "SNACKBAR EVENT: Erfolg.")
                    _uiEvent.send("Text erfolgreich in Firebase gespeichert!")
                }
                .onFailure { e ->
                    Log.e(tag, "SNACKBAR EVENT: Fehler: ${e.message}")
                    _uiEvent.send("Speichern fehlgeschlagen: ${e.message}")
                }
        }
    }

    fun setCloudRecognitionState(state: CloudRecognitionState) {
        _cloudRecognitionState.value = state
    }
}