package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TextViewModel : ViewModel() {

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText

    private val translator by lazy {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()
        Translation.getClient(options)
    }

    // Text vom CameraXManager verarbeiten
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
