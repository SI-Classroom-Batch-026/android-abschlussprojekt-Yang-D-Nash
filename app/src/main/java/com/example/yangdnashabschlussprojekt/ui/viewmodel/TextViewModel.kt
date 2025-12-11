package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.data.local.database.model.TextHistoryEntity
import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CloudRecognitionState {
    object Idle : CloudRecognitionState()
    object Loading : CloudRecognitionState()
    data class Success(val text: String) : CloudRecognitionState()
    data class Error(val message: String) : CloudRecognitionState()
}

class TextViewModel(
    private val visionRepository: VisionRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

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

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val translator by lazy {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()
        Translation.getClient(options)
    }

    fun updateBoundingBoxes(boxes: List<TimedBoundingBox>) {
        _boundingBoxes.value = boxes
    }

    private fun sortAndStructureText(blocks: List<Text.TextBlock>): String {
        if (blocks.isEmpty()) return ""

        val allLines = blocks.flatMap { it.lines }

        val sortedLines = allLines
            .sortedBy { it.boundingBox?.top ?: 0 }

        return sortedLines.joinToString("\n") { line ->
            line.text
        }
    }

    fun analyzeFrame(bitmap: Bitmap) {
        val currentTimestamp = System.currentTimeMillis()

        if (!_isAnalyzing.value || currentTimestamp - lastAnalyzedTimestamp < frameThrottleIntervalMs) {
            return
        }
        lastAnalyzedTimestamp = currentTimestamp

        if (_frameSize.value.width == 0) {
            _frameSize.value = Size(bitmap.width, bitmap.height)
        }

        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                val textBlocks = visionText.textBlocks
                if (textBlocks.isEmpty()) {
                    return@addOnSuccessListener
                }

                val structuredText = sortAndStructureText(textBlocks)

                if (structuredText.isBlank()) return@addOnSuccessListener

                _recognizedText.value = structuredText

                val boxes = textBlocks.map { block ->
                    val rect: Rect = block.boundingBox ?: Rect(0, 0, 0, 0)
                    TimedBoundingBox(
                        id = block.hashCode(),
                        label = block.text,
                        left = rect.left.toFloat(),
                        top = rect.top.toFloat(),
                        right = rect.right.toFloat(),
                        bottom = rect.bottom.toFloat(),
                        timestamp = currentTimestamp,
                        color = Color.Magenta,
                        frameWidth = bitmap.width,
                        frameHeight = bitmap.height
                    )
                }
                _boundingBoxes.value = boxes

                _isAnalyzing.value = false

                translateTextAsync(structuredText)
            }
            .addOnFailureListener {
                _recognizedText.value = "Text-Erkennung (ML Kit) fehlgeschlagen"
            }
    }

    fun continueAnalysis() {
        _isAnalyzing.value = true
        _recognizedText.value = ""
        _translatedText.value = ""
        _boundingBoxes.value = emptyList()
    }

    fun recognizeText(text: String) {
        _recognizedText.value = text
        translateTextAsync(text)
    }

    fun recognizeTextViaCloud(base64Image: String) {
        _isAnalyzing.value = false
        if (_cloudRecognitionState.value is CloudRecognitionState.Loading) return

        _cloudRecognitionState.value = CloudRecognitionState.Loading

        viewModelScope.launch {
            try {
                val response = visionRepository.detectText(base64Image)

                val cloudText = response.responses.firstOrNull()
                    ?.fullTextAnnotation
                    ?.text
                    ?: "Kein Text von der Cloud Vision API gefunden."

                _cloudRecognitionState.value = CloudRecognitionState.Success(cloudText)

                _recognizedText.value = cloudText
                translateTextAsync(cloudText)

            } catch (e: Exception) {
                _cloudRecognitionState.value = CloudRecognitionState.Error("Cloud-Erkennung fehlgeschlagen: ${e.localizedMessage}")
            } finally {
                _isAnalyzing.value = true
            }
        }
    }

    private fun translateTextAsync(text: String) {
        viewModelScope.launch {
            translator.downloadModelIfNeeded()
                .addOnSuccessListener {
                    translator.translate(text)
                        .addOnSuccessListener { translated ->
                            _translatedText.value = translated
                        }
                        .addOnFailureListener { _translatedText.value = "Übersetzung fehlgeschlagen" }
                }
                .addOnFailureListener { _translatedText.value = "Modell-Download fehlgeschlagen" }
        }
    }

    fun saveCurrentTextToHistory() {
        if (_recognizedText.value.isBlank()) return

        viewModelScope.launch {
            val entity = TextHistoryEntity(
                recognizedText = _recognizedText.value,
                translatedText = _translatedText.value,
                timestamp = System.currentTimeMillis()
            )
            historyRepository.saveEntry(entity)
        }
    }
}