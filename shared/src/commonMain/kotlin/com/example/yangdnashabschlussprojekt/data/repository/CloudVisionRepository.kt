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
        val apiKey = requireApiKey()
            ?: return Result.failure(IllegalStateException("Kein CLOUD_VISION_API_KEY konfiguriert."))
        val cleanBase64 = requireImageContent(base64Image)
            ?: return Result.failure(IllegalArgumentException("Kein Bild fuer Cloud-OCR vorhanden."))

        return runCatching {
            val response = executeVisionRequest(
                apiKey = apiKey,
                cleanBase64 = cleanBase64,
                features = listOf(VisionFeature(type = "DOCUMENT_TEXT_DETECTION"))
            )
            val text = normalizeDocumentText(response.fullTextAnnotation?.text.orEmpty())
            if (text.isBlank()) {
                error("Kein Text im importierten Bild erkannt.")
            }
            text
        }
    }

    suspend fun detectScene(base64Image: String): Result<SceneDetection> {
        val apiKey = requireApiKey()
            ?: return Result.failure(IllegalStateException("Kein CLOUD_VISION_API_KEY konfiguriert."))
        val cleanBase64 = requireImageContent(base64Image)
            ?: return Result.failure(IllegalArgumentException("Kein Bild fuer die Objekterkennung vorhanden."))

        return runCatching {
            val response = executeVisionRequest(
                apiKey = apiKey,
                cleanBase64 = cleanBase64,
                features = listOf(
                    VisionFeature(type = "OBJECT_LOCALIZATION", maxResults = 5),
                    VisionFeature(type = "LABEL_DETECTION", maxResults = 5),
                    VisionFeature(type = "LOGO_DETECTION", maxResults = 3)
                )
            )

            val candidates = buildList {
                response.logoAnnotations
                    .sortedByDescending { it.score }
                    .mapTo(this) { annotation -> annotation.description }
                response.localizedObjectAnnotations
                    .sortedByDescending { it.score }
                    .mapTo(this) { annotation -> annotation.name }
                response.labelAnnotations
                    .sortedByDescending { it.score }
                    .mapTo(this) { annotation -> annotation.description }
            }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()

            if (candidates.isEmpty()) {
                error("Kein Objekt oder Motiv im Bild erkannt.")
            }

            SceneDetection(
                primaryLabel = candidates.first(),
                candidates = candidates.take(6)
            )
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

    private fun requireApiKey(): String? {
        val apiKey = config.apiKey()?.trim().orEmpty()
        return apiKey.ifBlank { null }
    }

    private fun requireImageContent(base64Image: String): String? {
        val cleanBase64 = base64Image.substringAfter(",", base64Image).trim()
        return cleanBase64.ifBlank { null }
    }

    private suspend fun executeVisionRequest(
        apiKey: String,
        cleanBase64: String,
        features: List<VisionFeature>
    ) = transport.postJson(
        url = "https://vision.googleapis.com/v1/images:annotate?key=$apiKey",
        body = json.encodeToString(
            VisionApiRequest(
                requests = listOf(
                    AnnotateImageRequest(
                        image = VisionImage(content = cleanBase64),
                        features = features
                    )
                )
            )
        )
    ).let { responseJson ->
        val response = json.decodeFromString(VisionApiResponse.serializer(), responseJson)
        response.responses.firstOrNull()
            ?.also { firstResponse ->
                firstResponse.error?.message?.let { message ->
                    error(message)
                }
            }
            ?: error("Keine Antwort von der Vision API erhalten.")
    }
}

data class SceneDetection(
    val primaryLabel: String,
    val candidates: List<String>
)
