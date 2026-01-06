package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.speech.tts.TextToSpeech
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
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class TextViewModel(
    private val visionRepository: VisionRepository,
    private val userRepository: UserRepository,
    context: Context
) : ViewModel(), ImageAnalysis.Analyzer {

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes = _boundingBoxes.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText = _recognizedText.asStateFlow()

    private val _translatedText = MutableStateFlow("")
    val translatedText = _translatedText.asStateFlow()

    private val _isSingleBlockMode = MutableStateFlow(false)
    val isSingleBlockMode = _isSingleBlockMode.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _cloudRecognitionState = MutableStateFlow<CloudRecognitionState>(CloudRecognitionState.Idle)
    val cloudRecognitionState = _cloudRecognitionState.asStateFlow()

    private var tts: TextToSpeech? = null
    private val localRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val translator = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()
    )

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.GERMAN
        }
    }
    private fun formatText(text: String): String = text
        .replace(Regex("(?<=\\w)\\n(?=\\w)"), " ")
        .replace(Regex("\\n+"), "\n")
        .trim()
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(image: ImageProxy) {
        if (!_isAnalyzing.value) {
            image.close()
            return
        }
        val mediaImage = image.image
        if (mediaImage != null) {
            val rotation = image.imageInfo.rotationDegrees
            val inputImage = InputImage.fromMediaImage(mediaImage, rotation)
            localRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val isRotated = rotation == 90 || rotation == 270
                    val frameW = if (isRotated) image.height else image.width
                    val frameH = if (isRotated) image.width else image.height
                    _boundingBoxes.value = visionText.textBlocks.map { block ->
                        TimedBoundingBox(
                            id = block.hashCode(),
                            label = block.text,
                            left = block.boundingBox?.left?.toFloat() ?: 0f,
                            top = block.boundingBox?.top?.toFloat() ?: 0f,
                            right = block.boundingBox?.right?.toFloat() ?: 0f,
                            bottom = block.boundingBox?.bottom?.toFloat() ?: 0f,
                            timestamp = System.currentTimeMillis(),
                            color = Color(0xFF00E5FF),
                            frameWidth = frameW,
                            frameHeight = frameH
                        )
                    }
                    if (visionText.text.isNotBlank()) {
                        _recognizedText.value = formatText(visionText.text)
                        viewModelScope.launch { _uiEvent.emit("Text gefunden!") }
                    } else {
                        viewModelScope.launch { _uiEvent.emit("Kein Text erkannt") }
                    }
                    _isAnalyzing.value = false
                }
                .addOnFailureListener {
                    viewModelScope.launch { _uiEvent.emit("Fehler beim lokalen Scan") }
                    _isAnalyzing.value = false
                }
                .addOnCompleteListener { image.close() }
        } else {
            image.close()
        }
    }
    fun triggerLocalScan() {
        _boundingBoxes.value = emptyList()
        _recognizedText.value = ""
        _isAnalyzing.value = true
    }
    fun recognizeTextViaCloud(base64Image: String) {
        _isAnalyzing.value = false
        _cloudRecognitionState.value = CloudRecognitionState.Loading
        viewModelScope.launch {
            try {
                val response = visionRepository.analyzeImage(base64Image, listOf(Feature(type = "DOCUMENT_TEXT_DETECTION")))
                val annotation = response.responses.firstOrNull()?.fullTextAnnotation ?: return@launch
                val page = annotation.pages.firstOrNull()

                _boundingBoxes.value = annotation.pages.flatMap { p ->
                    p.blocks.map { block ->
                        val blockText = block.paragraphs.flatMap { para ->
                            para.words.map { word -> word.symbols.joinToString("") { it.text } }
                        }.joinToString(" ")
                        val v = block.boundingBox.vertices
                        TimedBoundingBox(
                            id = block.hashCode(),
                            label = blockText,
                            left = v.getOrNull(0)?.x?.toFloat() ?: 0f,
                            top = v.getOrNull(0)?.y?.toFloat() ?: 0f,
                            right = v.getOrNull(2)?.x?.toFloat() ?: 0f,
                            bottom = v.getOrNull(2)?.y?.toFloat() ?: 0f,
                            timestamp = System.currentTimeMillis(),
                            color = Color(0xFF00FFCC),
                            frameWidth = page?.width ?: 1,
                            frameHeight = page?.height ?: 1
                        )
                    }
                }
                val cleanText = formatText(annotation.text)
                _recognizedText.value = cleanText
                _cloudRecognitionState.value = CloudRecognitionState.Success(cleanText)
                _uiEvent.emit("Cloud Scan erfolgreich!")
                translateAndSpeak(cleanText)
            } catch (e: Exception) {
                _cloudRecognitionState.value = CloudRecognitionState.Error(e.message ?: "Fehler")
                _uiEvent.emit("Cloud Fehler: ${e.message}")
            }
        }
    }
    fun onBoxClicked(box: TimedBoundingBox) {
        if (box.label.isNotBlank()) {
            stopAudio()
            val selectedText = formatText(box.label)
            _recognizedText.value = selectedText
            _isSingleBlockMode.value = true
            translateAndSpeak(selectedText)
        }
    }
    private fun translateAndSpeak(text: String) {
        viewModelScope.launch {
            try {
                translator.downloadModelIfNeeded().await()
                val translation = translator.translate(text).await()
                _translatedText.value = translation
                tts?.speak(translation, TextToSpeech.QUEUE_FLUSH, null, null)
            } catch (_: Exception) {
                _translatedText.value = "Fehler"
            }
        }
    }
    fun stopAudio() { tts?.stop() }
    fun continueAnalysis() {
        stopAudio()
        _isAnalyzing.value = false
        _boundingBoxes.value = emptyList()
        _recognizedText.value = ""
        _translatedText.value = ""
        _cloudRecognitionState.value = CloudRecognitionState.Idle
        _isSingleBlockMode.value = false
    }
    fun onSaveToCloudClicked() {
        viewModelScope.launch {
            userRepository.saveToFirestore(_recognizedText.value, _translatedText.value)
                .onSuccess { _uiEvent.emit("Backup erfolgreich gespeichert!") }
                .onFailure { _uiEvent.emit("Fehler beim Cloud Backup") }
        }
    }
    fun recognizeText(newText: String) {
        if (newText.isNotBlank()) {
            val cleanText = formatText(newText)
            _recognizedText.value = cleanText
            translateAndSpeak(cleanText)
        }
    }
    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        translator.close()
        localRecognizer.close()
    }
}