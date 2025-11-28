package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.graphics.DetectedObject
import com.example.yangdnashabschlussprojekt.util.CloudVisionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ARViewModel : ViewModel() {

    // Flow für die erkannten Objekte
    private val _detectedObjects = MutableStateFlow<List<DetectedObject>>(emptyList())
    val detectedObjects: StateFlow<List<DetectedObject>> = _detectedObjects

    /**
     * Analysiert ein Frame (Bitmap) und updated den StateFlow.
     */
    fun analyzeFrame(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {  // Default statt IO für CPU-intensive Arbeit
            try {
                val results: List<DetectedObject> = CloudVisionHelper.detectObjects(bitmap)
                _detectedObjects.value = results
            } catch (e: Exception) {
                e.printStackTrace()
                // Optional: Fehlerhandling, z.B. leere Liste oder StateFlow Error State
            }
        }
    }
}
