package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedBoundingBoxes(boxes: List<com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox>) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        boxes.forEach { box ->
            val scaleX = size.width / box.frameWidth
            val scaleY = size.height / box.frameHeight
            drawRoundRect(
                color = Color.Cyan.copy(alpha = alpha),
                topLeft = Offset(box.left * scaleX, box.top * scaleY),
                size = Size((box.right - box.left) * scaleX, (box.bottom - box.top) * scaleY),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
            drawRoundRect(
                color = Color.Cyan.copy(alpha = 0.1f),
                topLeft = Offset(box.left * scaleX, box.top * scaleY),
                size = Size((box.right - box.left) * scaleX, (box.bottom - box.top) * scaleY),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            )
        }
    }
}