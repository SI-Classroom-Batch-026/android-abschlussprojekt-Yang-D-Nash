package com.example.yangdnashabschlussprojekt.ui.component.overlay

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
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

    val density = LocalDensity.current
    val fontSizePx = with(density) { 10.dp.toPx() }

    // --- ANIMATIONS ---
    val infiniteTransition = rememberInfiniteTransition(label = "HUD")

    // Pulsierender Rand für den "Scanning" Effekt
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "pulse"
    )

    // Ein rotierender Scan-Strich
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)), label = "scan"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (boxes.isEmpty()) return@Canvas

        // Skalierung berechnen (wie gehabt, aber stabil)
        val firstBox = boxes.first()
        val scale = maxOf(size.width / firstBox.frameWidth, size.height / firstBox.frameHeight)
        val offsetX = (size.width - firstBox.frameWidth * scale) / 2f
        val offsetY = (size.height - firstBox.frameHeight * scale) / 2f

        boxes.forEach { box ->
            val left = box.left * scale + offsetX
            val top = box.top * scale + offsetY
            val width = (box.right - box.left) * scale
            val height = (box.bottom - box.top) * scale
            val color = box.color // Cyan oder Magenta

            // 1. Cyber-Ecken zeichnen
            val cornerLen = width * 0.2f
            val hudPath = Path().apply {
                // Oben Links
                moveTo(left, top + cornerLen); lineTo(left, top); lineTo(left + cornerLen, top)
                // Oben Rechts
                moveTo(left + width - cornerLen, top); lineTo(left + width, top); lineTo(left + width, top + cornerLen)
                // Unten Rechts
                moveTo(left + width, top + height - cornerLen); lineTo(left + width, top + height); lineTo(left + width - cornerLen, top + height)
                // Unten Links
                moveTo(left + cornerLen, top + height); lineTo(left, top + height); lineTo(left, top + height - cornerLen)
            }

            // Glow-Effekt (leicht versetzt und transparent)
            drawPath(hudPath, color.copy(alpha = 0.3f * pulse), style = Stroke(width = 8f, cap = StrokeCap.Round))
            drawPath(hudPath, color, style = Stroke(width = 3f, cap = StrokeCap.Round))

            // 2. Bewegte Scan-Linie innerhalb der Box
            val lineY = top + (height * scanLineY)
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, color.copy(alpha = 0.6f), Color.Transparent)
                ),
                start = Offset(left, lineY),
                end = Offset(left + width, lineY),
                strokeWidth = 2f
            )

            // 3. High-Tech Label Background
            if (box.label.isNotBlank()) {
                val labelText = box.label.uppercase()

                // Hintergrund für Text (Schräge Kante für Sci-Fi Look)
                val labelPath = Path().apply {
                    moveTo(left, top - 5f)
                    lineTo(left + (width * 0.7f), top - 5f)
                    lineTo(left + (width * 0.75f), top - 25f)
                    lineTo(left, top - 25f)
                    close()
                }
                drawPath(labelPath, color.copy(alpha = 0.8f))

                // Text mit nativem Canvas für Schatten & Typeface
                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        this.color = android.graphics.Color.BLACK
                        textSize = fontSizePx
                        typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    }
                    drawText(labelText, left + 10f, top - 12f, paint)
                }
            }
        }
    }
}