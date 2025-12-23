package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "y"
    )
    Box(Modifier.fillMaxSize().zIndex(15f)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .graphicsLayer { translationY = yOffset * size.height }
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Transparent, laserColor, Color.Transparent)
                    )
                )
        )
    }
}