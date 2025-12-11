package com.example.yangdnashabschlussprojekt.data.local.database.model.box

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "text_history")
data class TextHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recognizedText: String,
    val translatedText: String,
    val timestamp: Long,
    val sourceLanguage: String = "English",
    val targetLanguage: String = "German"
)