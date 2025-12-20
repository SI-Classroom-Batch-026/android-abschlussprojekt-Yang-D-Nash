package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as ComposeSize // Alias für Compose Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import kotlin.math.max
import kotlin.math.min
import android.util.Size as AndroidSize // Alias für CameraX Size

@OptIn(ExperimentalTextApi::class)
@Composable
fun AnimatedBoxView(
    boxes: List<TimedBoundingBox>,
    frameSize: AndroidSize, // Nutzt jetzt den eindeutigen Alias
    isTextMode: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val infiniteTransition = rememberInfiniteTransition(label = "CyberHud")
    val pulseAlpha by infiniteTransition.animateFloat(
        0.4f, 1f,
        infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "Pulse"
    )
    val scanLineY by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "Scan"
    )

    val themeColor = if (isTextMode) Color.Magenta else Color.Cyan

    BoxWithConstraints(modifier.fillMaxSize()) {
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }

        val scale = max(screenW / frameSize.width, screenH / frameSize.height)
        val dx = (screenW - frameSize.width * scale) / 2
        val dy = (screenH - frameSize.height * scale) / 2

        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        Canvas(Modifier.fillMaxSize()) {
            boxes.forEach { box ->
                val left = box.left * scale + dx
                val top = box.top * scale + dy
                val width = (box.right - box.left) * scale
                val height = (box.bottom - box.top) * scale

                // Cyber-Ecken Pfad
                val corner = min(width, height) * 0.2f
                val path = Path().apply {
                    moveTo(left, top + corner); lineTo(left, top); lineTo(left + corner, top)
                    moveTo(left + width - corner, top); lineTo(left + width, top); lineTo(left + width, top + corner)
                    moveTo(left + width, top + height - corner); lineTo(left + width, top + height); lineTo(left + width - corner, top + height)
                    moveTo(left + corner, top + height); lineTo(left, top + height); lineTo(left, top + height - corner)
                }

                // Zeichnen mit Glüheffekt
                drawPath(path, themeColor.copy(alpha = 0.3f * pulseAlpha), style = Stroke(12f, cap = StrokeCap.Round))
                drawPath(path, themeColor, style = Stroke(4f, cap = StrokeCap.Round))

                // Scan-Linie bei Cloud-Aktivität (Long.MAX_VALUE Marker)
                if (box.timestamp == Long.MAX_VALUE) {
                    val y = top + (height * scanLineY)
                    drawLine(
                        Brush.horizontalGradient(listOf(Color.Transparent, themeColor, Color.Transparent)),
                        Offset(left, y), Offset(left + width, y), 4f
                    )
                }

                // Label-Tag
                if (box.label.isNotBlank()) {
                    val textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    val result = textMeasurer.measure(box.label.uppercase(), textStyle)

                    // Hintergrund-Box für Text
                    drawRect(
                        color = themeColor,
                        topLeft = Offset(left, top - result.size.height - 20f),
                        size = ComposeSize(result.size.width + 30f, result.size.height + 10f)
                    )
                    drawText(result, topLeft = Offset(left + 15f, top - result.size.height - 15f))
                }
            }
        }
    }
}