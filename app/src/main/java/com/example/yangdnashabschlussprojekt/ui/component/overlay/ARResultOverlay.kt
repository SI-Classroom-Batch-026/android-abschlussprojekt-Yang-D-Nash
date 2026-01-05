package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox

@Composable
fun ARResultOverlay(boxes: List<TimedBoundingBox>) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

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

            val hudColor = if (box.id == 999) Color(0xFF00FFCC) else Color(0xFF00E5FF)
            val strokePx = 2.5.dp.toPx()
            val bracketSize = 20.dp.toPx()

            drawLine(hudColor, Offset(left, top), Offset(left + bracketSize, top), strokePx)
            drawLine(hudColor, Offset(left, top), Offset(left, top + bracketSize), strokePx)

            drawLine(hudColor, Offset(right, top), Offset(right - bracketSize, top), strokePx)
            drawLine(hudColor, Offset(right, top), Offset(right, top + bracketSize), strokePx)

            drawLine(hudColor, Offset(left, bottom), Offset(left + bracketSize, bottom), strokePx)
            drawLine(hudColor, Offset(left, bottom), Offset(left, bottom - bracketSize), strokePx)

            drawLine(hudColor, Offset(right, bottom), Offset(right - bracketSize, bottom), strokePx)
            drawLine(hudColor, Offset(right, bottom), Offset(right, bottom - bracketSize), strokePx)

            if (box.label.isNotBlank()) {
                val textLayoutResult = textMeasurer.measure(box.label, labelStyle)
                val textWidth = textLayoutResult.size.width.toFloat()
                val textHeight = textLayoutResult.size.height.toFloat()

                val padding = 8f

                drawRect(
                    color = hudColor,
                    topLeft = Offset(left, top - textHeight - padding),
                    size = Size(textWidth + (padding * 2), textHeight + padding)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = box.label,
                    style = labelStyle,
                    topLeft = Offset(left + padding, top - textHeight - (padding / 2))
                )
            }
        }
    }
}