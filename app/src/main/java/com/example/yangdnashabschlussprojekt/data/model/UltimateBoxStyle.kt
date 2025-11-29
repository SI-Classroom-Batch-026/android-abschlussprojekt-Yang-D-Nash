package com.example.yangdnashabschlussprojekt.data.model

import androidx.compose.ui.graphics.Color

data class UltimateBoxStyle(
    val colors: List<Color> = listOf(Color.Red, Color.Yellow, Color.Green),
    val strokeWidth: Float = 3f,
    val textSize: Float = 40f,
    val glow: Boolean = true,
    val minGlowAlpha: Float = 0.2f,
    val maxGlowAlpha: Float = 0.6f,
    val pulseDuration: Int = 1500,
    val glowRadius: Int = 8,
    val rotationDegrees: Float = 0f,
    val skewX: Float = 0f,
    val skewY: Float = 0f,
    val particleCount: Int = 20, // Anzahl Partikel
    val physicsEnabled: Boolean = true // sanfte Bewegungen
)