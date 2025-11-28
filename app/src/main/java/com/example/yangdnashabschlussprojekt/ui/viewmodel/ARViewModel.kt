package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ARViewModel : ViewModel() {

    private val _detectedObjects = MutableStateFlow<List<String>>(emptyList())
    val detectedObjects: StateFlow<List<String>> = _detectedObjects

    fun scanObjects() {
        _detectedObjects.value = listOf("Apfel", "Banane", "Flasche")
    }
}
