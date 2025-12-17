package com.example.yangdnashabschlussprojekt.data.local.database.model.box

data class HistoryItem(
    val id: Long,
    val recognizedText: String,
    val translatedText: String,
    val timestampFormatted: String,
    val rawTimestamp: Long
)