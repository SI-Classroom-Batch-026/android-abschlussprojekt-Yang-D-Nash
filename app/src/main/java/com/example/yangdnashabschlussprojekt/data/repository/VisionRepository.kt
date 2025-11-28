package com.example.yangdnashabschlussprojekt.data.repository

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Base64
import com.example.yangdnashabschlussprojekt.data.api.VisionRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class VisionRepository(
    private val api: VisionApiService,
    private val apiKey: String
) {

    private val detector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .enableMultipleObjects()
            .enableClassification()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .build()
    )

    suspend fun detectObjects(image: InputImage): List<DetectedObjectResult> {
        return try {
            detectWithMLKit(image)
        } catch (e: Exception) {
            detectWithCloudVision(image)
        }
    }

    private suspend fun detectWithMLKit(image: InputImage): List<DetectedObjectResult> {
        val objects = detector.process(image).await()

        return objects.map {
            val label = it.labels.firstOrNull()
            DetectedObjectResult(
                name = label?.text ?: "Unbekannt",
                confidence = label?.confidence ?: 0f,
                boundingBox = Rect(it.boundingBox)
            )
        }
    }

    private suspend fun detectWithCloudVision(image: InputImage): List<DetectedObjectResult> {
        val base64 = imageToBase64(image)
        val request = VisionRequest.createLabelDetection(base64)

        val response = api.analyze(apiKey, request)
        val labels = response.responses.firstOrNull()?.labelAnnotations ?: emptyList()

        return labels.mapIndexed { index, label ->
            DetectedObjectResult(
                name = label.description,
                confidence = label.score,
                boundingBox = Rect(
                    50 + index * 40,
                    50,
                    300 + index * 40,
                    300
                )
            )
        }
    }

    private fun imageToBase64(image: InputImage): String {
        val bitmap = image.bitmapInternal ?: return ""
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP)
    }
}

data class DetectedObjectResult(
    val name: String,
    val confidence: Float,
    val boundingBox: Rect
)
