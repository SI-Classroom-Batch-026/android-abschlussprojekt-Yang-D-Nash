package com.example.yangdnashabschlussprojekt.data.repository

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

interface CloudTranslateConfig {
    fun apiKey(): String?
}

interface CloudTranslateTransport {
    suspend fun postJson(url: String, body: String): String
}

interface TargetLanguageProvider {
    fun currentLanguageCode(): String
}

class CloudTranslateRepository(
    private val config: CloudTranslateConfig,
    private val transport: CloudTranslateTransport,
    private val targetLanguageProvider: TargetLanguageProvider
) {
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    suspend fun translate(
        text: String,
        sourceLanguage: String? = null
    ): Result<String> {
        val apiKey = config.apiKey()?.trim().orEmpty()
        if (apiKey.isBlank()) {
            return Result.failure(IllegalStateException("Kein CLOUD_TRANSLATE_API_KEY konfiguriert."))
        }

        val cleanText = text.trim()
        if (cleanText.isBlank()) {
            return Result.success("")
        }

        val targetLanguage = targetLanguageProvider.currentLanguageCode()
            .trim()
            .ifBlank { "en" }

        return runCatching {
            val payload = buildJsonObject {
                put("q", cleanText)
                put("target", targetLanguage)
                put("format", "text")
                sourceLanguage
                    ?.trim()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { put("source", it) }
            }

            val responseBody = transport.postJson(
                url = "https://translation.googleapis.com/language/translate/v2?key=$apiKey",
                body = json.encodeToString(JsonObject.serializer(), payload)
            )

            val root = json.parseToJsonElement(responseBody).jsonObject
            val translation = root["data"]
                ?.jsonObject
                ?.get("translations")
                ?.jsonArray
                ?.firstOrNull()
                ?.jsonObject
                ?.get("translatedText")
                ?.jsonPrimitive
                ?.content
                .orEmpty()
                .trim()

            if (translation.isBlank()) {
                error("Keine Uebersetzung erhalten.")
            }

            decodeHtmlEntities(translation)
        }
    }

    private fun decodeHtmlEntities(text: String): String {
        return text
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
    }
}
