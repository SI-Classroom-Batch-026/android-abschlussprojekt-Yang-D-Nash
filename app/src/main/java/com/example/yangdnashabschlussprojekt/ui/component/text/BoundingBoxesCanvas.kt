package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun BoundingBoxesCanvas(
    boundingBoxes: List<TimedBoundingBox>,
    pulseDurationMs: Int = 800,
    cornerRadius: Float = 12f,
    strokeWidth: Float = 3f,
    glowColor: Color = Color.Cyan
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDurationMs),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val viewWidth = size.width
        val viewHeight = size.height
        val now = System.currentTimeMillis()

        boundingBoxes.forEach { box ->
            val age = now - box.timestamp
            if (age > 2000) return@forEach
            val alpha = pulseAlpha

            val left = box.rect.left.toFloat() / box.bitmapWidth * viewWidth
            val top = box.rect.top.toFloat() / box.bitmapHeight * viewHeight
            val right = box.rect.right.toFloat() / box.bitmapWidth * viewWidth
            val bottom = box.rect.bottom.toFloat() / box.bitmapHeight * viewHeight
            val width = right - left
            val height = bottom - top

            val glowAlphas = listOf(0.36f, 0.24f, 0.12f)
            for ((i, a) in glowAlphas.withIndex()) {
                val offset = i + 1f
                drawRoundRect(
                    color = glowColor.copy(alpha = a),
                    topLeft = Offset(left - offset, top - offset),
                    size = Size(width + 2*offset, height + 2*offset),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                    style = Stroke(width = strokeWidth)
                )
            }

            drawRoundRect(
                color = box.color.copy(alpha = alpha),
                topLeft = Offset(left, top),
                size = Size(width, height),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(width = strokeWidth)
            )
        }
    }
}
