package com.example.yangdnashabschlussprojekt.util

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions

class ObjectDetectionAnalyzer(
    private val listener: DetectionListener
) : ImageAnalysis.Analyzer {

    private var objectDetector: ObjectDetector
    private var isProcessing = false

    init {
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        objectDetector = ObjectDetection.getClient(options)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        isProcessing = true
        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        objectDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                listener.onObjectsDetected(detectedObjects)
            }
            .addOnFailureListener { e ->
                Log.e("ObjectDetection", "Failed", e)
            }
            .addOnCompleteListener {
                isProcessing = false
                imageProxy.close()
            }
    }

    interface DetectionListener {
        fun onObjectsDetected(objects: List<com.google.mlkit.vision.objects.DetectedObject>)
    }
}
