package com.example.yangdnashabschlussprojekt.ui.component.overlay

import android.graphics.RectF
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox

@Composable
fun TextBoundingBoxOverlay(
    boxes: List<TimedBoundingBox>,
    onBoxClicked: (TimedBoundingBox) -> Unit
) {
    val transition = rememberInfiniteTransition(label = "Pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Alpha"
    )
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(boxes) {
                detectTapGestures { tapOffset ->
                    boxes.forEach { box ->
                        val rect = calculateTransformedRect(box, size.width.toFloat(), size.height.toFloat())
                        if (rect.contains(tapOffset.x, tapOffset.y)) {
                            onBoxClicked(box)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        boxes.forEach { box ->
            val rect = calculateTransformedRect(box, size.width, size.height)

            val isCloud = box.color == Color(0xFF00FFCC)
            val color = box.color
            drawRect(
                color = color.copy(alpha = if (isCloud) 0.2f else 0.1f),
                topLeft = Offset(rect.left, rect.top),
                size = Size(rect.width(), rect.height())
            )
            drawRect(
                color = color.copy(alpha = alpha),
                topLeft = Offset(rect.left, rect.top),
                size = Size(rect.width(), rect.height()),
                style = Stroke(
                    width = if (isCloud) 3.dp.toPx() else 2.dp.toPx(),
                    pathEffect = if (isCloud) null else PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            )
        }
    }
}
private fun calculateTransformedRect(box: TimedBoundingBox, canvasW: Float, canvasH: Float): RectF {
    val imageW = if (box.frameWidth <= 0) 1f else box.frameWidth.toFloat()
    val imageH = if (box.frameHeight <= 0) 1f else box.frameHeight.toFloat()

    val scale = maxOf(canvasW / imageW, canvasH / imageH)
    val offsetX = (canvasW - imageW * scale) / 2f
    val offsetY = (canvasH - imageH * scale) / 2f

    return RectF(
        box.left * scale + offsetX,
        box.top * scale + offsetY,
        box.right * scale + offsetX,
        box.bottom * scale + offsetY
    )
}