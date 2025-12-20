package com.example.yangdnashabschlussprojekt.data.remote.repository

import com.example.yangdnashabschlussprojekt.data.remote.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class VisionRepository(
    private val apiKey: String,
    private val api: VisionApiService
) {
    suspend fun analyzeImage(
        base64Image: String,
        features: List<Feature>
    ): VisionApiResponse = withContext(Dispatchers.IO) {
        withTimeout(5000L) {
            val cleanBase64 = if (base64Image.contains(",")) {
                base64Image.split(",")[1]
            } else base64Image
            val requestBody = VisionApiRequest(
                requests = listOf(
                    AnnotateImageRequest(
                        image = Image(content = cleanBase64),
                        features = features
                    )
                )
            )
            api.annotateImage(apiKey = apiKey, request = requestBody)
        }
    }
}