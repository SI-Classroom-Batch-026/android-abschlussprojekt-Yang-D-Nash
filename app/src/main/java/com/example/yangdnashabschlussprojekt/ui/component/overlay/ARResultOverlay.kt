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
        boxes.forEach { box ->
            val scaleX = size.width / box.frameWidth
            val scaleY = size.height / box.frameHeight

            val left = box.left * scaleX
            val top = box.top * scaleY
            val width = (box.right - box.left) * scaleX
            val height = (box.bottom - box.top) * scaleY

            val hudColor = if (box.id == 999) Color(0xFF00FFCC) else Color(0xFF00E5FF)
            val strokePx = 2.5.dp.toPx()
            val bracketSize = 20.dp.toPx()

            drawLine(hudColor, Offset(left, top), Offset(left + bracketSize, top), strokePx)
            drawLine(hudColor, Offset(left, top), Offset(left, top + bracketSize), strokePx)
            drawLine(hudColor, Offset(left + width, top), Offset(left + width - bracketSize, top), strokePx)
            drawLine(hudColor, Offset(left + width, top), Offset(left + width, top + bracketSize), strokePx)
            drawLine(hudColor, Offset(left, top + height), Offset(left + bracketSize, top + height), strokePx)
            drawLine(hudColor, Offset(left, top + height), Offset(left, top + height - bracketSize), strokePx)
            drawLine(hudColor, Offset(left + width, top + height), Offset(left + width - bracketSize, top + height), strokePx)
            drawLine(hudColor, Offset(left + width, top + height), Offset(left + width, top + height - bracketSize), strokePx)

            if (box.label.isNotBlank()) {
                val textLayoutResult = textMeasurer.measure(box.label, labelStyle)
                val textWidth = textLayoutResult.size.width.toFloat()
                val textHeight = textLayoutResult.size.height.toFloat()

                drawRect(
                    color = hudColor,
                    topLeft = Offset(left, top - textHeight - 4.dp.toPx()),
                    size = Size(textWidth + 8.dp.toPx(), textHeight + 4.dp.toPx())
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = box.label,
                    style = labelStyle,
                    topLeft = Offset(left + 4.dp.toPx(), top - textHeight - 2.dp.toPx())
                )
            }
        }
    }
}