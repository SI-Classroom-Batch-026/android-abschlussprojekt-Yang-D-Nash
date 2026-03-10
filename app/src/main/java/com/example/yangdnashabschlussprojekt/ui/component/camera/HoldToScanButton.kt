package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun HoldToScanButton(
    onTrigger: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryContainerColor = MaterialTheme.colorScheme.secondaryContainer
    val onSecondaryContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
    val ringBgColor = Color.LightGray.copy(alpha = 0.2f)
    val myRipple = ripple(bounded = false, radius = 42.dp, color = primaryColor)
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.92f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )
    LaunchedEffect(isPressed, enabled) {
        if (!enabled) {
            progress.snapTo(0f)
            return@LaunchedEffect
        }
        if (isPressed) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
            if (progress.value == 1f) {
                try {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                } catch (_: Exception) {  }
                onTrigger()
                progress.snapTo(0f)
            }
        } else {
            progress.animateTo(0f, tween(250))
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(120.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()

            drawCircle(
                color = ringBgColor,
                style = Stroke(width = strokeWidth)
            )
            if (enabled) {
                drawArc(
                    color = if (progress.value >= 1f) Color.Green else primaryColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.value,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        Surface(
            onClick = { },
            enabled = enabled,
            interactionSource = interactionSource,
            shape = CircleShape,
            color = when {
                !enabled -> secondaryContainerColor.copy(alpha = 0.4f)
                isPressed -> primaryColor
                else -> secondaryContainerColor
            },
            shadowElevation = if (isPressed && enabled) 0.dp else 6.dp,
            modifier = Modifier
                .size(80.dp)
                .scale(buttonScale)
                .then(
                    if (enabled) {
                        Modifier.indication(interactionSource, myRipple)
                    } else {
                        Modifier
                    }
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = when {
                        !enabled -> onSecondaryContainerColor.copy(alpha = 0.5f)
                        isPressed -> Color.White
                        else -> onSecondaryContainerColor
                    }
                )
            }
        }
    }
}