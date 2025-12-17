package com.example.yangdnashabschlussprojekt.data.local.database.model.box

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory

@Entity(tableName = "text_history")
data class TextHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val sourceText: String,
    val translatedText: String,
    val timestamp: Long
)
fun TextHistory.toEntity() = TextHistoryEntity(
    id = id,
    sourceText = sourceText,
    translatedText = translatedText,
    timestamp = timestamp
)

fun TextHistoryEntity.toKmpModel() = TextHistory(
    id = id,
    sourceText = sourceText,
    translatedText = translatedText,
    timestamp = timestamp
)