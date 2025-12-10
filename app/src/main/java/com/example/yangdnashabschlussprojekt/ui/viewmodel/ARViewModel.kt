package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ARViewModel : ViewModel() {

    private val _boxes = MutableStateFlow<List<TimedBoundingBox>>(emptyList())
    val boxes: StateFlow<List<TimedBoundingBox>> = _boxes

    private val objectDetector by lazy {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        ObjectDetection.getClient(options)
    }

    fun analyzeFrame(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val frameWidth = bitmap.width
        val frameHeight = bitmap.height

        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->

                val newBoxes = detectedObjects.map { obj ->

                    TimedBoundingBox(
                        id = obj.trackingId ?: obj.hashCode(),
                        label = obj.labels.firstOrNull()?.text ?: "Objekt",
                        left = obj.boundingBox.left.toFloat(),
                        top = obj.boundingBox.top.toFloat(),
                        right = obj.boundingBox.right.toFloat(),
                        bottom = obj.boundingBox.bottom.toFloat(),
                        timestamp = System.currentTimeMillis(),
                        color = Color.Cyan,
                        frameWidth = frameWidth,
                        frameHeight = frameHeight
                    )
                }

                println("ARViewModel: Detected boxes: $newBoxes")

                _boxes.value = newBoxes
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }
}
