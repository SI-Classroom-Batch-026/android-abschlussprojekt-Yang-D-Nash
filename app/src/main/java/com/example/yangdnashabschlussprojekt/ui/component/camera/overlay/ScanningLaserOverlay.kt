package com.example.yangdnashabschlussprojekt.ui.component.camera.overlay

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun ScanningLaserOverlay(laserColor: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val yOffset by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "y"
    )

    Box(Modifier.fillMaxSize().zIndex(15f)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(40.dp)
                .graphicsLayer { translationY = yOffset * size.height }
                .background(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to laserColor.copy(alpha = 0.7f),
                        0.51f to Color.White,
                        0.52f to laserColor.copy(alpha = 0.7f),
                        1f to Color.Transparent
                    )
                )
        )
    }
}