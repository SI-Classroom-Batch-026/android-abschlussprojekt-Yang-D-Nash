package com.example.yangdnashabschlussprojekt.util

import android.graphics.Bitmap
import com.example.yangdnashabschlussprojekt.data.graphics.DetectedObject
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.tasks.await

object CloudVisionHelper {

    private val detector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE) // Echtzeit
            .enableMultipleObjects()
            .enableClassification() // Labels hinzufügen
            .build()
    )

    suspend fun detectObjects(bitmap: Bitmap): List<DetectedObject> {
        val image = InputImage.fromBitmap(bitmap, 0)
        val results = detector.process(image).await()

        return results.map { obj ->
            val label = obj.labels.firstOrNull()?.text ?: "Unknown"
            DetectedObject(
                boundingBox = obj.boundingBox,
                label = label,
                imageWidth = bitmap.width,
                imageHeight = bitmap.height,
                rotation = 0,
                trackingId = obj.trackingId
            )
        }
    }
}
