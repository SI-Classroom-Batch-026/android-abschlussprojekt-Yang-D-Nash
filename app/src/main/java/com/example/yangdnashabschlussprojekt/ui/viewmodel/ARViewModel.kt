package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ARViewModel : ViewModel() {

    // Beispiel: erkannte Objekte als StateFlow
    private val _detectedObjects = MutableStateFlow<List<String>>(emptyList())
    val detectedObjects: StateFlow<List<String>> = _detectedObjects

    // Simulierter Scan / Objekterkennung
    fun scanObjects() {
        // Hier später ML Kit Objekt-Erkennung integrieren
        _detectedObjects.value = listOf("Apfel", "Banane", "Flasche")
    }
}
