package com.example.yangdnashabschlussprojekt.ui.component.overlay

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
        initialValue = 0.3f,
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
                detectTapGestures { offset ->
                    boxes.forEach { box ->
                        val fW = if (box.frameWidth <= 0) 1 else box.frameWidth
                        val fH = if (box.frameHeight <= 0) 1 else box.frameHeight
                        val scale = maxOf(size.width / fW.toFloat(), size.height / fH.toFloat())
                        val ox = (size.width - fW * scale) / 2f
                        val oy = (size.height - fH * scale) / 2f

                        val l = box.left * scale + ox
                        val t = box.top * scale + oy
                        val r = box.right * scale + ox
                        val b = box.bottom * scale + oy

                        if (offset.x in l..r && offset.y in t..b) {
                            onBoxClicked(box)
                            return@detectTapGestures
                        }
                    }
                }
            }
    ) {
        boxes.forEach { box ->
            val fW = if (box.frameWidth <= 0) 1 else box.frameWidth
            val fH = if (box.frameHeight <= 0) 1 else box.frameHeight
            val scale = maxOf(size.width / fW.toFloat(), size.height / fH.toFloat())
            val ox = (size.width - fW * scale) / 2f
            val oy = (size.height - fH * scale) / 2f

            val l = box.left * scale + ox
            val t = box.top * scale + oy
            val r = box.right * scale + ox
            val b = box.bottom * scale + oy

            val isCloud = box.label != "LOCAL"
            val color = if (isCloud) Color(0xFF00FFCC) else Color(0xFF00E5FF)

            drawRect(
                color = color.copy(alpha = if (isCloud) alpha * 0.2f else 0.1f),
                topLeft = Offset(l, t),
                size = Size(r - l, b - t)
            )
            drawRect(
                color = color.copy(alpha = if (isCloud) alpha else 1f),
                topLeft = Offset(l, t),
                size = Size(r - l, b - t),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = if (isCloud) PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f) else null
                )
            )
        }
    }
}