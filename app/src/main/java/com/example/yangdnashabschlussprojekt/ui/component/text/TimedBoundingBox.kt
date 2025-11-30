package com.example.yangdnashabschlussprojekt.ui.component.text

import android.graphics.Rect
import androidx.compose.ui.graphics.Color

data class TimedBoundingBox(
    val rect: Rect,
    val timestamp: Long,
    val color: Color = Color.Magenta,
    val bitmapWidth: Int = 1280,
    val bitmapHeight: Int = 720
    )

