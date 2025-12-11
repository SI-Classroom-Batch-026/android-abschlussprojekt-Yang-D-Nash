package com.example.yangdnashabschlussprojekt.ui.component.history

data class HistoryItem(
    val id: Long,
    val recognizedText: String,
    val translatedText: String,
    val timestampFormatted: String,
    val rawTimestamp: Long
)

