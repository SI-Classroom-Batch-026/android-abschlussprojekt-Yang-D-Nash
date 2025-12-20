package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.util.Size
import androidx.compose.animation.core.Animatable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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

@Composable
fun AnimatedBoxView(boxes: List<TimedBoundingBox>, frameSize: Size, modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val infiniteTransition = rememberInfiniteTransition(label = "CyberHud")

    // Animationen
    val pulseAlpha by infiniteTransition.animateFloat(0.4f, 1f, infiniteRepeatable(tween(1000), RepeatMode.Reverse))
    val scanLineY by infiniteTransition.animateFloat(0f, 1f, infiniteRepeatable(tween(2000, easing = LinearEasing)))

    BoxWithConstraints(modifier.fillMaxSize()) {
        val screenW = with(density) { maxWidth.toPx() }
        val screenH = with(density) { maxHeight.toPx() }
        val scale = max(screenW / frameSize.width, screenH / frameSize.height)
        val dx = (screenW - frameSize.width * scale) / 2
        val dy = (screenH - frameSize.height * scale) / 2

        val alphaAnimatable = remember { Animatable(0f) }
        LaunchedEffect(boxes) {
            if (boxes.any { it.timestamp != Long.MAX_VALUE }) {
                alphaAnimatable.snapTo(1f)
                alphaAnimatable.animateTo(0f, tween(1000))
            }
        }

        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        Canvas(Modifier.fillMaxSize()) {
            boxes.forEach { box ->
                val isCloud = box.timestamp == Long.MAX_VALUE
                val alpha = if (isCloud) 1f else alphaAnimatable.value
                if (alpha <= 0f) return@forEach

                val left = box.left * scale + dx
                val top = box.top * scale + dy
                val w = (box.right - box.left) * scale
                val h = (box.bottom - box.top) * scale

                // 1. NEON BRACKETS
                val corner = min(w, h) * 0.25f
                val path = Path().apply {
                    moveTo(left, top + corner); lineTo(left, top); lineTo(left + corner, top)
                    moveTo(left + w - corner, top); lineTo(left + w, top); lineTo(left + w, top + corner)
                    moveTo(left + w, top + h - corner); lineTo(left + w, top + h); lineTo(left + w - corner, top + h)
                    moveTo(left + corner, top + h); lineTo(left, top + h); lineTo(left, top + h - corner)
                }
                // Glow & Line
                drawPath(path, box.color.copy(alpha = alpha * 0.3f * pulseAlpha), style = Stroke(15f, cap = StrokeCap.Round))
                drawPath(path, box.color.copy(alpha = alpha), style = Stroke(6f))

                // 2. SCANLINE (Nur Cloud)
                if (isCloud) {
                    val y = top + (h * scanLineY)
                    drawLine(
                        Brush.horizontalGradient(listOf(Color.Transparent, box.color.copy(0.5f), Color.Transparent)),
                        Offset(left, y), Offset(left + w, y), 4f
                    )
                }

                // 3. TYPEWRITER LABEL
                if (isCloud) {
                    val textLayout = textMeasurer.measure(
                        box.label.uppercase(),
                        TextStyle(color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    )
                    drawRect(box.color.copy(alpha * 0.9f), Offset(left, top - textLayout.size.height - 20f), androidx.compose.ui.geometry.Size(textLayout.size.width + 30f, textLayout.size.height + 10f))
                    drawText(
                        textLayoutResult = textLayout,
                        color = Color.Black, // <-- Das hat gefehlt
                        topLeft = Offset(left + 15f, top - textLayout.size.height - 15f)
                    )
                }
            }
        }
    }
}