package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.ui.overlay.AnimatedBox
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ARViewModel : ViewModel() {

    private val _boxes = MutableStateFlow<List<AnimatedBox>>(emptyList())
    val boxes: StateFlow<List<AnimatedBox>> = _boxes

    // STREAM_MODE für Live Tracking
    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .build()

    private val objectDetector = ObjectDetection.getClient(options)

    fun onBoxTapped(index: Int) {
        println("Tapped Box: $index")
    }

    fun analyzeFrame(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                val newBoxes = detectedObjects.map { obj ->
                    AnimatedBox(
                        id = obj.trackingId ?: obj.hashCode(),
                        label = obj.labels.firstOrNull()?.text ?: "Objekt",
                        targetLeft = obj.boundingBox.left.toFloat(),
                        targetTop = obj.boundingBox.top.toFloat(),
                        targetRight = obj.boundingBox.right.toFloat(),
                        targetBottom = obj.boundingBox.bottom.toFloat()
                    )
                }
                _boxes.value = newBoxes
            }
            .addOnFailureListener { e ->
                println("Object detection failed: ${e.message}")
            }
    }
}
