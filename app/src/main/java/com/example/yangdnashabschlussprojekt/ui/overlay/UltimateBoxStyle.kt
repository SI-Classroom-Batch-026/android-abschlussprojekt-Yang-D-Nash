package com.example.yangdnashabschlussprojekt.ui.overlay

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

data class UltimateBoxStyle(
    val colors: List<Color> = listOf(Color.Cyan, Color.Blue, Color.Magenta),
    val minGlowAlpha: Float = 0.3f,
    val maxGlowAlpha: Float = 0.8f,
    val pulseDuration: Int = 2000,
    val cornerRadius: Float = 20f,
    val glowStroke: Float = 6f,
    val mainStroke: Float = 3f,
    val textSize: Float = 42f
) {
    fun interpolateColor(t: Float): Color {
        if (colors.size == 1) return colors.first()
        val segment = 1f / (colors.size - 1)
        for (i in 0 until colors.size - 1) {
            val start = i * segment
            val end = (i + 1) * segment
            if (t in start..end) {
                val local = (t - start) / (end - start)
                return lerp(colors[i], colors[i + 1], local)
            }
        }
        return colors.last()
    }
}
