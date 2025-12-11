package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.util.Size
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import androidx.compose.ui.geometry.Size as ComposeSize

@Composable
fun AnimatedBoxView(
    boxes: List<TimedBoundingBox>,
    frameSize: Size,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val composableWidthPx = with(density) { maxWidth.toPx() }
        val composableHeightPx = with(density) { maxHeight.toPx() }

        val scaleX = if (frameSize.width > 0) composableWidthPx / frameSize.width else 0f
        val scaleY = if (frameSize.height > 0) composableHeightPx / frameSize.height else 0f

        val alphaAnimatable = remember { Animatable(0f) }

        LaunchedEffect(boxes) {
            if (boxes.isNotEmpty()) {
                alphaAnimatable.snapTo(1f)
                alphaAnimatable.animateTo(0f, animationSpec = tween(durationMillis = 3000, delayMillis = 50))
            }
        }

        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        Canvas(modifier = Modifier.fillMaxSize()) {
            val alpha = alphaAnimatable.value
            val glowColor = Color.White.copy(alpha = alpha * 0.5f) // Weißer Glow

            boxes.forEach { box ->
                val scaledLeft = box.left * scaleX
                val scaledTop = box.top * scaleY
                val scaledRight = box.right * scaleX
                val scaledBottom = box.bottom * scaleY

                val width = scaledRight - scaledLeft
                val height = scaledBottom - scaledTop

                val rectTopLeft = Offset(scaledLeft, scaledTop)
                val rectSize = ComposeSize(width, height)

                drawRect(
                    color = glowColor,
                    topLeft = rectTopLeft,
                    size = rectSize,
                    style = Stroke(width = 12f)
                )

                drawRect(
                    color = box.color.copy(alpha = alpha),
                    topLeft = rectTopLeft,
                    size = rectSize,
                    style = Stroke(width = 4f)
                )
            }
        }
    }
}