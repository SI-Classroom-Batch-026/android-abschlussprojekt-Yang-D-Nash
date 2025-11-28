package com.example.yangdnashabschlussprojekt.data.graphics

import android.graphics.Rect
import androidx.compose.animation.core.Animatable

data class DetectedObject(
    val boundingBox: Rect,
    val label: String,
    val imageWidth: Int,
    val imageHeight: Int,
    val rotation: Int,
    val trackingId: Int? = null

)

data class AnimatedBox(
    val label: String,
    val animLeft: Animatable<Float, *>,
    val animTop: Animatable<Float, *>,
    val animRight: Animatable<Float, *>,
    val animBottom: Animatable<Float, *>,
    var isNew: Boolean = true
)
