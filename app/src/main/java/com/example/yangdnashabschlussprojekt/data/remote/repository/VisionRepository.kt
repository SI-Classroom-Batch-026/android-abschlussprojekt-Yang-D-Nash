package com.example.yangdnashabschlussprojekt.data.remote.repository

import com.example.yangdnashabschlussprojekt.data.remote.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.AnnotateImageRequest
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Feature
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Image
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.VisionApiRequest
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.VisionApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class VisionRepository(
    private val apiKey: String,
    private val api: VisionApiService
) {

    suspend fun detectText(base64Image: String): VisionApiResponse = withContext(Dispatchers.IO) {
        withTimeout(15000L) {
            val requestBody = VisionApiRequest(
                requests = listOf(
                    AnnotateImageRequest(
                        image = Image(content = base64Image),
                        features = listOf(Feature(type = "DOCUMENT_TEXT_DETECTION"))
                    )
                )
            )

            api.annotateImage(
                apiKey = apiKey,
                request = requestBody
            )
        }
    }
}