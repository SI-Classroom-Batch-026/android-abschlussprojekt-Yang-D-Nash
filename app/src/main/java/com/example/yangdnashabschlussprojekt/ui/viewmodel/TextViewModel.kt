package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.model.box.TimedBoundingBox
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TextViewModel : ViewModel() {

    private val _boundingBoxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boundingBoxes: StateFlow<List<TimedBoundingBox>> = _boundingBoxes

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText

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

    fun analyzeFrame(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                _recognizedText.value = visionText.text

                val boxes = visionText.textBlocks.map { block ->

                    val rect: Rect = block.boundingBox ?: Rect(0, 0, 0, 0)

                    TimedBoundingBox(
                        id = block.hashCode(),
                        label = block.text,
                        left = rect.left.toFloat(),
                        top = rect.top.toFloat(),
                        right = rect.right.toFloat(),
                        bottom = rect.bottom.toFloat(),
                        timestamp = System.currentTimeMillis(),
                        color = Color.Magenta,
                        frameWidth = bitmap.width,
                        frameHeight = bitmap.height
                    )
                }

                _boundingBoxes.value = boxes
            }
            .addOnFailureListener {
                _recognizedText.value = "Text-Erkennung fehlgeschlagen"
            }
    }
    fun recognizeText(text: String) {
        _recognizedText.value = text
        translateTextAsync(text)
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
}
