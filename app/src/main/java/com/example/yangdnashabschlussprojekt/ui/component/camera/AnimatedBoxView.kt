package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.util.Size
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import kotlin.math.max
import kotlin.math.min

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun AnimatedBoxView(boxes: List<TimedBoundingBox>, frameSize: Size, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "CyberHud")

    val pulseAlpha by infiniteTransition.animateFloat(0.6f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse))
    val scanLineY by infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing)))

    BoxWithConstraints(modifier.fillMaxSize()) {
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }

        // Skalierung berechnen
        val scale = max(screenW / frameSize.width, screenH / frameSize.height)
        val dx = (screenW - frameSize.width * scale) / 2
        val dy = (screenH - frameSize.height * scale) / 2

        Canvas(Modifier.fillMaxSize()) {
            boxes.forEach { box ->
                val isCloud = box.timestamp == Long.MAX_VALUE

                // LIVE BOXEN sollen IMMER sichtbar sein (alpha = 1f)
                // CLOUD BOXEN sind ebenfalls 1f
                val alpha = 1f

                val left = box.left * scale + dx
                val top = box.top * scale + dy
                val w = (box.right - box.left) * scale
                val h = (box.bottom - box.top) * scale

                // 1. NEON BRACKETS (Ecken)
                val corner = min(w, h) * 0.25f
                val path = Path().apply {
                    moveTo(left, top + corner); lineTo(left, top); lineTo(left + corner, top)
                    moveTo(left + w - corner, top); lineTo(left + w, top); lineTo(left + w, top + corner)
                    moveTo(left + w, top + h - corner); lineTo(left + w, top + h); lineTo(left + w - corner, top + h)
                    moveTo(left + corner, top + h); lineTo(left, top + h); lineTo(left, top + h - corner)
                }

                drawPath(path, box.color.copy(alpha = alpha * 0.3f * pulseAlpha), style = Stroke(15f, cap = StrokeCap.Round))
                drawPath(path, box.color.copy(alpha = alpha), style = Stroke(6f))

                // 2. SCANLINE (Nur bei Cloud-Scan für den Effekt)
                if (isCloud) {
                    val y = top + (h * scanLineY)
                    drawLine(
                        Brush.horizontalGradient(listOf(Color.Transparent, box.color.copy(0.5f), Color.Transparent)),
                        Offset(left, y), Offset(left + w, y), 4f
                    )
                }

                // 3. LABEL ZEICHNEN (Jetzt für Cloud UND Live!)
                if (box.label.isNotBlank()) {
                    val textLayout = textMeasurer.measure(
                        box.label.uppercase(),
                        TextStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    )

                    val labelWidth = textLayout.size.width.toFloat()
                    val labelHeight = textLayout.size.height.toFloat()

                    // Hintergrund-Rechteck für das Label (Cyan/Magenta)
                    drawRect(
                        color = box.color.copy(alpha = 0.9f),
                        topLeft = Offset(left, top - labelHeight - 20f),
                        size = androidx.compose.ui.geometry.Size(labelWidth + 30f, labelHeight + 10f)
                    )

                    // Der eigentliche Text
                    drawText(
                        textLayoutResult = textLayout,
                        topLeft = Offset(left + 15f, top - labelHeight - 15f)
                    )
                }
            }
        }
    }
}