package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox

@Composable
fun TextBoundingBoxOverlay(boxes: List<TimedBoundingBox>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        boxes.forEach { box ->
            val fW = if (box.frameWidth <= 0) 1 else box.frameWidth
            val fH = if (box.frameHeight <= 0) 1 else box.frameHeight

            val scaleX = canvasWidth / fW.toFloat()
            val scaleY = canvasHeight / fH.toFloat()
            val scale = maxOf(scaleX, scaleY)

            val offsetX = (canvasWidth - fW * scale) / 2f
            val offsetY = (canvasHeight - fH * scale) / 2f

            val left = box.left * scale + offsetX
            val top = box.top * scale + offsetY
            val right = box.right * scale + offsetX
            val bottom = box.bottom * scale + offsetY

            val isCloud = box.label == "CLOUD"
            val boxColor = if (isCloud) Color(0xFF00FFCC) else Color(0xFF00E5FF)

            drawRect(
                color = boxColor.copy(alpha = if (isCloud) 0.25f else 0.15f),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top)
            )
            drawRect(
                color = boxColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(
                    width = if (isCloud) 2.dp.toPx() else 1.5.dp.toPx(),
                    pathEffect = if (isCloud) PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f) else null
                )
            )
        }
    }
}