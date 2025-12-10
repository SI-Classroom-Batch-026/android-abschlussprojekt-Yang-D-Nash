package com.example.yangdnashabschlussprojekt.data.local.database.model.box

import androidx.compose.ui.graphics.Color

data class TimedBoundingBox(
    val id: Int = 0,
    val label: String = "",
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val timestamp: Long,
    val color: Color,
    val frameWidth: Int,
    val frameHeight: Int
)
