package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TextViewModel : ViewModel() {

    // Beispiel: erkannter Text + Übersetzung
    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText

    private val _translatedText = MutableStateFlow("")
    val translatedText: StateFlow<String> = _translatedText

    // Funktion: Text erkennen
    fun recognizeText(text: String) {
        _recognizedText.value = text
        translateText(text)
    }

    // Funktion: Text übersetzen
    private fun translateText(text: String) {
        // Hier später Cloud Translation API oder ML Kit Translation integrieren
        _translatedText.value = "$text (übersetzt)"
    }
}
