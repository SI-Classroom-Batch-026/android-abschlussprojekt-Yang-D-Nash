package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.util.Size
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // NEU: Import für Theme-Farben
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import kotlin.math.max
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

        val scale = if (frameSize.width > 0 && frameSize.height > 0) {
            max(composableWidthPx / frameSize.width, composableHeightPx / frameSize.height)
        } else {
            0f
        }
        val dx = (composableWidthPx - frameSize.width * scale) / 2
        val dy = (composableHeightPx - frameSize.height * scale) / 2

        val alphaAnimatable = remember { Animatable(0f) }

        LaunchedEffect(boxes) {
            if (boxes.isNotEmpty()) {
                alphaAnimatable.snapTo(1f)
                alphaAnimatable.animateTo(0f, animationSpec = tween(durationMillis = 3000, delayMillis = 50))
            }
        }

        val frameAccentColor = MaterialTheme.colorScheme.tertiary

        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        Canvas(modifier = Modifier.fillMaxSize()) {
            val alpha = alphaAnimatable.value

            val animatedColor = frameAccentColor.copy(alpha = alpha)

            boxes.forEach { box ->
                val scaledLeft = box.left * scale + dx
                val scaledTop = box.top * scale + dy
                val scaledRight = box.right * scale + dx
                val scaledBottom = box.bottom * scale + dy

                val width = scaledRight - scaledLeft
                val height = scaledBottom - scaledTop

                val rectTopLeft = Offset(scaledLeft, scaledTop)
                val rectSize = ComposeSize(width, height)

                drawRect(
                    color = animatedColor,
                    topLeft = rectTopLeft,
                    size = rectSize,
                    style = Stroke(width = 6f)
                )

                 drawRect(
                    color = box.color.copy(alpha = alpha * 0.15f),
                     topLeft = rectTopLeft,
                     size = rectSize,
                )
            }
        }
    }
}