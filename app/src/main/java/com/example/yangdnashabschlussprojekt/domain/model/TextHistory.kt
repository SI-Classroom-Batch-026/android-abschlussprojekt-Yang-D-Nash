package com.example.yangdnashabschlussprojekt.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TextHistory(
    val id: Long? = null,
    val sourceText: String,
    val translatedText: String,
    val timestamp: Long
)