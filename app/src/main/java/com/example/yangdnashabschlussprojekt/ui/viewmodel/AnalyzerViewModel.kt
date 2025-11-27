package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AnalyzerViewModel() : ViewModel() {

    private val _detectedObjects = MutableStateFlow<List<String>>(emptyList())
    val detectedObjects: StateFlow<List<String>> = _detectedObjects
    }
