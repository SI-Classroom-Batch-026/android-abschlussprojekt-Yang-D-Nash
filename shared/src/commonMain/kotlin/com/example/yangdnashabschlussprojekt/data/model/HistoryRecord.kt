package com.example.yangdnashabschlussprojekt.data.model

import kotlinx.serialization.Serializable

data class HistoryRecord(
    val localId: Long? = null,
    val cloudId: String? = null,
    val sourceText: String,
    val translatedText: String,
    val timestamp: Long,
    val isFromCloud: Boolean = false
)

@Serializable
data class CloudHistoryDocument(
    val sourceText: String = "",
    val translatedText: String = "",
    val timestamp: Long = 0L
)
