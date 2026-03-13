package com.example.yangdnashabschlussprojekt.data.companion

import android.os.Build
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.feature.model.CompanionMode
import com.example.yangdnashabschlussprojekt.feature.model.CompanionSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private const val DEFAULT_COMPANION_PORT = 45872

class DesktopCompanionClient(
    private val settingsRepository: SettingsRepository,
    private val okHttpClient: OkHttpClient
) {
    private val json = Json {
        encodeDefaults = true
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    fun configuredHost(): String = settingsRepository.getDesktopCompanionHost()

    suspend fun connect(hostInput: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val normalizedHost = normalizeBaseUrl(hostInput)
            require(normalizedHost.isNotBlank()) { "Bitte zuerst eine Desktop-Adresse eintragen." }
            settingsRepository.setDesktopCompanionHost(normalizedHost)
            performStatusCheck(normalizedHost)
            "Desktop Companion verbunden: $normalizedHost"
        }
    }

    suspend fun publishSnapshot(snapshot: CompanionSnapshot): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val baseUrl = settingsRepository.getDesktopCompanionHost()
            require(baseUrl.isNotBlank()) { "Kein Desktop Companion konfiguriert." }

            val request = Request.Builder()
                .url("$baseUrl/api/companion/snapshot")
                .post(
                    json.encodeToString(snapshot)
                        .toRequestBody("application/json; charset=utf-8".toMediaType())
                )
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                require(response.isSuccessful) {
                    response.body.string().ifBlank { "Desktop Companion konnte Snapshot nicht empfangen." }
                }
            }
            Unit
        }
    }

    suspend fun publishStatus(
        mode: CompanionMode,
        statusMessage: String,
        recognizedText: String? = null,
        translatedText: String? = null
    ) {
        publishSnapshot(
            CompanionSnapshot(
                deviceName = Build.MODEL ?: "Android",
                sourcePlatform = "Android",
                activeMode = mode,
                updatedAtEpochMillis = System.currentTimeMillis(),
                statusMessage = statusMessage,
                recognizedText = recognizedText,
                translatedText = translatedText
            )
        )
    }

    private fun performStatusCheck(baseUrl: String) {
        val request = Request.Builder()
            .url("$baseUrl/api/companion/status")
            .get()
            .build()

        okHttpClient.newCall(request).execute().use { response ->
            require(response.isSuccessful) {
                response.body.string().ifBlank { "Desktop Companion antwortet nicht." }
            }
        }
    }

    private fun normalizeBaseUrl(rawInput: String): String {
        var normalized = rawInput.trim()
        if (normalized.isBlank()) return ""
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://$normalized"
        }

        normalized = normalized.trimEnd('/')

        val hostPort = normalized.substringAfter("://")
        val hasPort = hostPort.substringBefore("/").contains(":")
        if (!hasPort) {
            normalized += ":$DEFAULT_COMPANION_PORT"
        }

        return normalized
    }
}
