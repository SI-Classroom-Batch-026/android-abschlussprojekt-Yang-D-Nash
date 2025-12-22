package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun BoxesOverlay(
    arViewModel: ARViewModel,
    textViewModel: TextViewModel,
    isTextMode: Boolean,
) {
    val boxes by if (isTextMode) textViewModel.boundingBoxes.collectAsState()
    else arViewModel.boundingBoxes.collectAsState()

    val textMeasurer = rememberTextMeasurer()

    // --- ANIMATIONS ---
    val infiniteTransition = rememberInfiniteTransition(label = "HUD")

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse"
    )

    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "scan"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (boxes.isEmpty()) return@Canvas

        // Skalierung berechnen
        val firstBox = boxes.first()
        val scale = maxOf(size.width / firstBox.frameWidth, size.height / firstBox.frameHeight)
        val offsetX = (size.width - firstBox.frameWidth * scale) / 2f
        val offsetY = (size.height - firstBox.frameHeight * scale) / 2f

        boxes.forEach { box ->
            val left = box.left * scale + offsetX
            val top = box.top * scale + offsetY
            val width = (box.right - box.left) * scale
            val height = (box.bottom - box.top) * scale
            val color = box.color

            // 1. Cyber-Ecken (HUD Style) - nutzt jetzt direkt 30.dp.toPx() im DrawScope
            val cornerLen = (width * 0.2f).coerceAtMost(30.dp.toPx())
            val hudPath = Path().apply {
                moveTo(left, top + cornerLen); lineTo(left, top); lineTo(left + cornerLen, top)
                moveTo(left + width - cornerLen, top); lineTo(left + width, top); lineTo(left + width, top + cornerLen)
                moveTo(left + width, top + height - cornerLen); lineTo(left + width, top + height); lineTo(left + width - cornerLen, top + height)
                moveTo(left + cornerLen, top + height); lineTo(left, top + height); lineTo(left, top + height - cornerLen)
            }

            // Glow-Effekt
            drawPath(hudPath, color.copy(alpha = 0.3f * pulse), style = Stroke(width = 8f, cap = StrokeCap.Round))
            drawPath(hudPath, color, style = Stroke(width = 3f, cap = StrokeCap.Round))

            // 2. Animierte Scan-Linie
            val lineY = top + (height * scanLineY)
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, color.copy(alpha = 0.6f), Color.Transparent)
                ),
                start = Offset(left, lineY),
                end = Offset(left + width, lineY),
                strokeWidth = 2.dp.toPx()
            )

            // 3. Label Zeichnen
            if (box.label.isNotBlank()) {
                val labelText = box.label.uppercase()
                val textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                val textLayoutResult = textMeasurer.measure(text = labelText, style = textStyle)
                val labelWidth = textLayoutResult.size.width.toFloat() + 40f
                val labelHeight = 20.dp.toPx()

                // Hintergrund-Trapezoid
                val labelPath = Path().apply {
                    moveTo(left, top)
                    lineTo(left + labelWidth, top)
                    lineTo(left + labelWidth + 15f, top - labelHeight)
                    lineTo(left, top - labelHeight)
                    close()
                }

                drawPath(labelPath, color.copy(alpha = 0.8f))

                // Text zeichnen
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(left + 10f, top - labelHeight + (labelHeight - textLayoutResult.size.height) / 2)
                )
            }
        }
    }
}