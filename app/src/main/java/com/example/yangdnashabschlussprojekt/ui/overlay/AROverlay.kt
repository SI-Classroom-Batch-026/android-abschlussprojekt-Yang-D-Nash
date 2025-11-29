package com.example.yangdnashabschlussprojekt.ui.overlay

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.launch

@Composable
fun AROverlay(
    boxes: List<AnimatedBox>,
    style: UltimateBoxStyle = UltimateBoxStyle(),
    cameraWidth: Float,
    cameraHeight: Float,
    onBoxTap: ((Int) -> Unit)? = null
) {
    val scope = rememberCoroutineScope()

    val infinite = rememberInfiniteTransition()

    val glowAlpha by infinite.animateFloat(
        initialValue = style.minGlowAlpha,
        targetValue = style.maxGlowAlpha,
        animationSpec = infiniteRepeatable(tween(style.pulseDuration), RepeatMode.Reverse)
    )

    val colorFraction by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(style.pulseDuration), RepeatMode.Reverse)
    )

    val shimmerAlpha by infinite.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(style.pulseDuration / 2), RepeatMode.Reverse)
    )

    LaunchedEffect(boxes) {
        boxes.forEach { box ->
            scope.launch {
                box.updateTarget(
                    box.animLeft.targetValue,
                    box.animTop.targetValue,
                    box.animRight.targetValue,
                    box.animBottom.targetValue
                )
            }
        }
    }

    Box(
        modifier = Modifier.pointerInput(boxes) {
            detectTapGestures { offset ->
                val scaleX = size.width / cameraWidth
                val scaleY = size.height / cameraHeight

                boxes.forEachIndexed { index, box ->
                    val left = box.animLeft.value * scaleX
                    val right = box.animRight.value * scaleX
                    val top = box.animTop.value * scaleY
                    val bottom = box.animBottom.value * scaleY

                    if (offset.x in left..right && offset.y in top..bottom) {
                        onBoxTap?.invoke(index)
                    }
                }
            }
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scaleX = size.width / cameraWidth
            val scaleY = size.height / cameraHeight
            val currentColor = style.interpolateColor(colorFraction)

            boxes.forEach { box ->
                drawUltimateBox(
                    box = box,
                    style = style,
                    currentColor = currentColor,
                    glowAlpha = glowAlpha,
                    shimmerAlpha = shimmerAlpha,
                    scaleX = scaleX,
                    scaleY = scaleY
                )
            }
        }
    }
}
