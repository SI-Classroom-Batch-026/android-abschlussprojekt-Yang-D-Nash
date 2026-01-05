package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Feature
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
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
    fun recognizeTextViaCloud(base64Image: String) {
        _isAnalyzing.value = false
        _isSingleBlockMode.value = false
        _cloudRecognitionState.value = CloudRecognitionState.Loading
        viewModelScope.launch {
            try {
                val response = visionRepository.analyzeImage(base64Image, listOf(Feature(type = "DOCUMENT_TEXT_DETECTION")))
                val annotation = response.responses.firstOrNull()?.fullTextAnnotation ?: return@launch

                val ts = System.currentTimeMillis()
                val page = annotation.pages.firstOrNull()
                val cloudWidth = page?.width ?: 1
                val cloudHeight = page?.height ?: 1

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
                            timestamp = ts,
                            color = Color(0xFF00FFCC),
                            frameWidth = cloudWidth,
                            frameHeight = cloudHeight
                        )
                    }
                }
                val cleanText = formatText(annotation.text)
                _recognizedText.value = cleanText
                _cloudRecognitionState.value = CloudRecognitionState.Success(cleanText)
                translateAndSpeak(cleanText)

            } catch (e: Exception) {
                _cloudRecognitionState.value = CloudRecognitionState.Error(e.message ?: "Fehler")
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
                .onSuccess { _uiEvent.emit("Cloud Backup OK!") }
                .onFailure { _uiEvent.emit("Fehler beim Backup") }
        }
    }
    fun toggleLiveDetection() { _isAnalyzing.value = !_isAnalyzing.value }
    fun recognizeText(newText: String) {
        if (newText.isNotBlank()) {
            val cleanText = formatText(newText)
            _recognizedText.value = cleanText
            translateAndSpeak(cleanText)
        }
    }
    override fun analyze(image: ImageProxy) { image.close() }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        translator.close()
    }
}