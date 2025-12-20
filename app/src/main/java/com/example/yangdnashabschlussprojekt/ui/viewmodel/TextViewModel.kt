package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Rect
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
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
    private val manageHistoryUseCase: ManageHistoryUseCase,
    private val userRepository: UserRepository
) : ViewModel() {
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
    private val recognizer = com.google.mlkit.vision.text.TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val translator by lazy {
        Translation.getClient(TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build())
    }
    fun recognizeTextViaCloud(base64Image: String) {
        if (_cloudRecognitionState.value is CloudRecognitionState.Loading) return
        _isAnalyzing.value = false
        viewModelScope.launch {
            _cloudRecognitionState.value = CloudRecognitionState.Loading
            try {
                val response = visionRepository.analyzeImage(base64Image, listOf(Feature(type = "DOCUMENT_TEXT_DETECTION")))
                val cloudText = response.responses.firstOrNull()?.fullTextAnnotation?.text ?: throw IllegalStateException("Kein Text gefunden")
                _recognizedText.value = cloudText
                _cloudRecognitionState.value = CloudRecognitionState.Success(cloudText)
                _uiEvent.send("Cloud-Scan erfolgreich!")
                translateTextSuspend(cloudText)
            } catch (e: Exception) {
                _cloudRecognitionState.value = CloudRecognitionState.Error(e.localizedMessage ?: "Fehler")
                _uiEvent.send("Cloud-Fehler: ${e.message}")
            }
        }
    }
    fun onSaveToCloudClicked() {
        if (_recognizedText.value.isBlank()) return
        viewModelScope.launch {
            userRepository.saveTextEntry(_recognizedText.value, _translatedText.value)
                .onSuccess { _uiEvent.send("In Firebase gespeichert!") }
                .onFailure { _uiEvent.send("Fehler beim Cloud-Speichern") }
        }
    }
    fun saveCurrentTextToHistory() {
        if (_recognizedText.value.isBlank()) return
        viewModelScope.launch {
            manageHistoryUseCase.save(
                TextHistory(
                    null,
                    _recognizedText.value,
                    _translatedText.value,
                    System.currentTimeMillis()
                )
            )
            _uiEvent.send("Lokal gespeichert.")
        }
    }
    @OptIn(ExperimentalGetImage::class)
    fun analyzeImageProxy(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        if (!_isAnalyzing.value || currentTimestamp - lastAnalyzedTimestamp < 200L) {
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
        val rotation = imageProxy.imageInfo.rotationDegrees
        _frameSize.value = Size(imageProxy.width, imageProxy.height)
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val detectedText = visionText.text.trim()
                if (detectedText.length > 3 && detectedText != _recognizedText.value) {
                    lastAnalyzedTimestamp = currentTimestamp
                    _recognizedText.value = detectedText
                    translateTextAsync(detectedText)
                    updateBoundingBoxes(visionText.textBlocks, imageProxy.width, imageProxy.height, currentTimestamp)
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
    private fun translateTextAsync(text: String) {
        viewModelScope.launch {
            translateTextSuspend(text)
        }
    }
    private suspend fun translateTextSuspend(text: String) {
        if (text.isBlank()) return
        try {
            translator.downloadModelIfNeeded().await()
            _translatedText.value = translator.translate(text).await()
        } catch (_: Exception) {
            _translatedText.value = "Übersetzungsfehler"
        }
    }
    private fun updateBoundingBoxes(blocks: List<Text.TextBlock>, w: Int, h: Int, ts: Long) {
        _boundingBoxes.value = blocks
            .filter { it.text.length > 3 }
            .map { block ->
                val r = block.boundingBox ?: Rect(0,0,0,0)
                TimedBoundingBox(
                    id = block.text.hashCode(),
                    label = block.text.take(15),
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
    fun continueAnalysis() {
        _recognizedText.value = ""
        _translatedText.value = ""
        _boundingBoxes.value = emptyList()
        _cloudRecognitionState.value = CloudRecognitionState.Idle
        _isAnalyzing.value = true
    }
    override fun onCleared() {
        super.onCleared()
        translator.close()
        recognizer.close()
    }
    fun loadFromHistory(recognized: String, translated: String) {
        _isAnalyzing.value = false
        _recognizedText.value = recognized
        _translatedText.value = translated
        _cloudRecognitionState.value = CloudRecognitionState.Success(recognized)
        viewModelScope.launch {
            _uiEvent.send("Eintrag aus Historie geladen")
        }
    }
    fun setCloudRecognitionState(state: CloudRecognitionState) {
        _cloudRecognitionState.value = state
    }
    fun recognizeText(newText: String) {
        _isAnalyzing.value = false
        _recognizedText.value = newText
        translateTextAsync(newText)
    }
}