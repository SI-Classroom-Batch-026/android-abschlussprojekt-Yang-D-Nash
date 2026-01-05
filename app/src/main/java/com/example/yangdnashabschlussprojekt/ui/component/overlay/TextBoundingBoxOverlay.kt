package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox

@Composable
fun TextBoundingBoxOverlay(boxes: List<TimedBoundingBox>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        boxes.forEach { box ->
            val scaleX = canvasWidth / box.frameWidth
            val scaleY = canvasHeight / box.frameHeight
            val scale = maxOf(scaleX, scaleY)

            val offsetX = (canvasWidth - box.frameWidth * scale) / 2f
            val offsetY = (canvasHeight - box.frameHeight * scale) / 2f

            val left = box.left * scale + offsetX
            val top = box.top * scale + offsetY
            val right = box.right * scale + offsetX
            val bottom = box.bottom * scale + offsetY

            val boxColor = Color(0xFF00E5FF)
            
            drawRect(
                color = boxColor.copy(alpha = 0.15f),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top)
            )
            drawRect(
                color = boxColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}