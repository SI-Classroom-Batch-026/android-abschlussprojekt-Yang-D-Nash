package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Feature
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TextViewModel(
    private val visionRepository: VisionRepository,
    private val manageHistoryUseCase: ManageHistoryUseCase,
    private val userRepository: UserRepository
) : ViewModel(), ImageAnalysis.Analyzer {

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

    private val _isAnalyzing = MutableStateFlow(true)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private var lastAnalyzedTimestamp = 0L
    private val TEXT_FPS_LIMIT = 150L
    private var recognitionJob: Job? = null

    private val recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val translator by lazy {
        Translation.getClient(TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build())
    }
    override fun analyze(image: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (!_isAnalyzing.value || currentTimestamp - lastAnalyzedTimestamp < TEXT_FPS_LIMIT) {
            image.close()
            return
        }
        lastAnalyzedTimestamp = currentTimestamp
        analyzeImageProxy(image)
    }
    @OptIn(ExperimentalGetImage::class)
    private fun analyzeImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        val rotation = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotation)

        val isPortrait = rotation == 90 || rotation == 270
        val frameWidth = if (isPortrait) imageProxy.height else imageProxy.width
        val frameHeight = if (isPortrait) imageProxy.width else imageProxy.height

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                updateBoundingBoxes(visionText.textBlocks, frameWidth, frameHeight, System.currentTimeMillis())

                val detectedText = visionText.text.trim()
                if (detectedText.length > 3 && detectedText != _recognizedText.value) {
                    triggerTextProcessing(detectedText)
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    private fun triggerTextProcessing(text: String) {
        recognitionJob?.cancel()
        recognitionJob = viewModelScope.launch {
            delay(500)
            _recognizedText.value = text
            translateTextSuspend(text)
        }
    }

    private fun updateBoundingBoxes(blocks: List<Text.TextBlock>, w: Int, h: Int, ts: Long) {
        _boundingBoxes.value = blocks.filter { it.text.length > 2 }.map { block ->
            val r = block.boundingBox ?: Rect(0,0,0,0)
            TimedBoundingBox(
                id = block.text.hashCode(),
                label = "",
                left = r.left.toFloat(),
                top = r.top.toFloat(),
                right = r.right.toFloat(),
                bottom = r.bottom.toFloat(),
                timestamp = ts,
                color = Color.Cyan,
                frameWidth = w,
                frameHeight = h
            )
        }
    }

    fun saveCurrentTextToHistory() {
        val original = _recognizedText.value
        val translated = _translatedText.value
        if (original.isBlank()) return

        viewModelScope.launch {
            try {
                val historyEntry = TextHistory(
                    id = null,
                    sourceText = original,
                    translatedText = translated,
                    timestamp = System.currentTimeMillis()
                )
                manageHistoryUseCase.save(historyEntry)
                _uiEvent.send("Lokal gespeichert!")
            } catch (e: Exception) {
                _uiEvent.send("Fehler: ${e.message}")
            }
        }
    }

    fun onSaveToCloudClicked() {
        val original = _recognizedText.value
        val translated = _translatedText.value

        viewModelScope.launch {
            userRepository.saveToFirestore(
                sourceText = original, // Korrigiert
                translatedText = translated
            )
                .onSuccess { _uiEvent.send("Cloud Backup OK! ☁️") }
                .onFailure { _uiEvent.send("Cloud Fehler") }
        }
    }

    fun recognizeTextViaCloud(base64Image: String) {
        if (_cloudRecognitionState.value is CloudRecognitionState.Loading) return
        pauseAnalysis()
        viewModelScope.launch {
            _cloudRecognitionState.value = CloudRecognitionState.Loading
            try {
                val response = visionRepository.analyzeImage(base64Image, listOf(Feature(type = "DOCUMENT_TEXT_DETECTION")))
                val cloudText = response.responses.firstOrNull()?.fullTextAnnotation?.text ?: ""
                _recognizedText.value = cloudText
                _cloudRecognitionState.value = CloudRecognitionState.Success(cloudText)
                translateTextSuspend(cloudText)
            } catch (e: Exception) {
                _cloudRecognitionState.value = CloudRecognitionState.Error(e.message ?: "Cloud Error")
                continueAnalysis()
            }
        }
    }

    private suspend fun translateTextSuspend(text: String) {
        if (text.isBlank()) return
        try {
            translator.downloadModelIfNeeded().await()
            _translatedText.value = translator.translate(text).await()
        } catch (_: Exception) {
            _translatedText.value = "Übersetzung fehlgeschlagen"
        }
    }

    fun toggleLiveDetection() {
        if (_isAnalyzing.value) pauseAnalysis() else continueAnalysis()
    }

    fun pauseAnalysis() {
        _isAnalyzing.value = false
        _boundingBoxes.value = emptyList()
    }

    fun continueAnalysis() {
        _isAnalyzing.value = true
        _cloudRecognitionState.value = CloudRecognitionState.Idle
    }

    fun recognizeText(newText: String) {
        _recognizedText.value = newText
        viewModelScope.launch { translateTextSuspend(newText) }
    }

    override fun onCleared() {
        super.onCleared()
        translator.close()
        recognizer.close()
    }
}