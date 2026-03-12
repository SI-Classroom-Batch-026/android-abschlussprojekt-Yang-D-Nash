package com.example.yangdnashabschlussprojekt.data.repository

import com.example.yangdnashabschlussprojekt.data.model.vision.AnnotateImageRequest
import com.example.yangdnashabschlussprojekt.data.model.vision.VisionApiRequest
import com.example.yangdnashabschlussprojekt.data.model.vision.VisionApiResponse
import com.example.yangdnashabschlussprojekt.data.model.vision.VisionFeature
import com.example.yangdnashabschlussprojekt.data.model.vision.VisionImage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface CloudVisionConfig {
    fun apiKey(): String?
}

interface CloudVisionTransport {
    suspend fun postJson(url: String, body: String): String
}

class CloudVisionRepository(
    private val config: CloudVisionConfig,
    private val transport: CloudVisionTransport
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    suspend fun extractDocumentText(base64Image: String): Result<String> {
        val apiKey = config.apiKey()?.trim().orEmpty()
        if (apiKey.isBlank()) {
            return Result.failure(
                IllegalStateException("Kein CLOUD_VISION_API_KEY konfiguriert.")
            )
        }

        val cleanBase64 = base64Image.substringAfter(",", base64Image).trim()
        if (cleanBase64.isBlank()) {
            return Result.failure(
                IllegalArgumentException("Kein Bild fuer Cloud-OCR vorhanden.")
            )
        }

        return runCatching {
            val requestBody = VisionApiRequest(
                requests = listOf(
                    AnnotateImageRequest(
                        image = VisionImage(content = cleanBase64),
                        features = listOf(VisionFeature(type = "DOCUMENT_TEXT_DETECTION"))
                    )
                )
            )
            val responseJson = transport.postJson(
                url = "https://vision.googleapis.com/v1/images:annotate?key=$apiKey",
                body = json.encodeToString(requestBody)
            )
            val response = json.decodeFromString(VisionApiResponse.serializer(), responseJson)
            val firstResponse = response.responses.firstOrNull()
                ?: error("Keine Antwort von der Vision API erhalten.")
            firstResponse.error?.message?.let { message ->
                error(message)
            }
            val text = normalizeDocumentText(firstResponse.fullTextAnnotation?.text.orEmpty())
            if (text.isBlank()) {
                error("Kein Text im importierten Bild erkannt.")
            }
            text
        }
    }

    private fun normalizeDocumentText(rawText: String): String {
        return rawText
            .split('\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(" ")
            .replace(Regex("\\s+"), " ")
    }
}
