package com.example.yangdnashabschlussprojekt.ui.overlay

import android.graphics.Paint
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun DrawScope.drawUltimateBox(
    box: AnimatedBox,
    style: UltimateBoxStyle,
    currentColor: Color,
    glowAlpha: Float,
    shimmerAlpha: Float,
    scope: CoroutineScope,
    scaleX: Float = 1f,
    scaleY: Float = 1f
) {
    scope.launch {
        box.updateTarget(
            box.animLeft.value,
            box.animTop.value,
            box.animRight.value,
            box.animBottom.value
        )
    }

    // Skaliere die Box-Koordinaten auf Canvas-Größe
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

    drawContext.canvas.nativeCanvas.drawText(
        box.label,
        left,
        top - 12f,
        Paint().apply {
            color = currentColor.copy(alpha = shimmerAlpha).toArgb()
            textSize = style.textSize
            isFakeBoldText = true
        }
    )
}
