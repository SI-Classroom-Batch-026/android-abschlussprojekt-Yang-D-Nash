package com.example.yangdnashabschlussprojekt.feature.model

import kotlinx.serialization.Serializable

@Serializable
enum class CompanionMode {
    AR,
    TEXT,
    HISTORY,
    SETTINGS
}

@Serializable
data class CompanionHistoryEntry(
    val recognizedText: String,
    val translatedText: String = "",
    val timestampMillis: Long
)

@Serializable
data class CompanionSnapshot(
    val deviceName: String,
    val sourcePlatform: String,
    val activeMode: CompanionMode,
    val updatedAtEpochMillis: Long,
    val statusMessage: String? = null,
    val recognizedObject: String? = null,
    val objectCandidates: List<String> = emptyList(),
    val recognizedText: String? = null,
    val translatedText: String? = null,
    val historyItems: List<CompanionHistoryEntry> = emptyList()
)
