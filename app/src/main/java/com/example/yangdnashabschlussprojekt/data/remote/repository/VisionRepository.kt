package com.example.yangdnashabschlussprojekt.data.remote.repository

import com.example.yangdnashabschlussprojekt.data.remote.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.AnnotateImageRequest
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Feature
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.Image
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.VisionApiRequest
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.VisionApiResponse

class VisionRepository(
    private val apiKey: String,
    private val api: VisionApiService
) {

    suspend fun detectText(base64Image: String): VisionApiResponse {
        val requestBody = VisionApiRequest(
            requests = listOf(
                AnnotateImageRequest(
                    image = Image(content = base64Image),
                    features = listOf(Feature(type = "DOCUMENT_TEXT_DETECTION"))
                )
            )
        )

        return api.annotateImage(
            apiKey = apiKey,
            request = requestBody
        )
    }
}