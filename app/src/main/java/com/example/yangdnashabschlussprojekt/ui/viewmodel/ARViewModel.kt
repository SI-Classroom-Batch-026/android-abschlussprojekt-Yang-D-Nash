package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
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

    private val objectDetector by lazy {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
        ObjectDetection.getClient(options)
    }

    fun updateBoxes(newBoxes: List<AnimatedBox>) {
        _boxes.value = newBoxes
    }

    @Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    fun analyzeFrame(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            objectDetector.process(image)
                .addOnSuccessListener { detectedObjects ->
                    val newBoxes = detectedObjects.map { obj ->
                        AnimatedBox(
                            id = obj.trackingId ?: obj.hashCode(),
                            label = obj.labels.firstOrNull()?.text ?: "Objekt",
                            left = obj.boundingBox.left.toFloat(),
                            top = obj.boundingBox.top.toFloat(),
                            right = obj.boundingBox.right.toFloat(),
                            bottom = obj.boundingBox.bottom.toFloat()
                        )
                    }
                    updateBoxes(newBoxes)
                }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}

