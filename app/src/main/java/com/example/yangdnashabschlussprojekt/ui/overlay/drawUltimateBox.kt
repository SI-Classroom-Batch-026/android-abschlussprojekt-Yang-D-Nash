package com.example.yangdnashabschlussprojekt.ui.overlay

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import android.graphics.Paint

fun DrawScope.drawUltimateBox(
    box: AnimatedBox,
    style: UltimateBoxStyle,
    currentColor: Color,
    glowAlpha: Float,
    shimmerAlpha: Float,
    scaleX: Float = 1f,
    scaleY: Float = 1f
) {
    val left = box.animLeft.value * scaleX
    val top = box.animTop.value * scaleY
    val right = box.animRight.value * scaleX
    val bottom = box.animBottom.value * scaleY

    drawRoundRect(
        color = currentColor.copy(alpha = glowAlpha),
        topLeft = Offset(left - 6f, top - 6f),
        size = Size((right - left) + 12f, (bottom - top) + 12f),
        cornerRadius = CornerRadius(style.cornerRadius, style.cornerRadius),
        style = Stroke(width = style.glowStroke)
    )

    drawRoundRect(
        color = currentColor,
        topLeft = Offset(left, top),
        size = Size(right - left, bottom - top),
        cornerRadius = CornerRadius(style.cornerRadius, style.cornerRadius),
        style = Stroke(width = style.mainStroke)
    )

    val textPaint = Paint().apply {
        color = currentColor.copy(alpha = shimmerAlpha).toArgb()
        textSize = style.textSize
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }

    val x = (left + right) / 2
    val y = (top + bottom) / 2 - (textPaint.descent() + textPaint.ascent()) / 2
    drawContext.canvas.nativeCanvas.drawText(box.label, x, y, textPaint)
}
