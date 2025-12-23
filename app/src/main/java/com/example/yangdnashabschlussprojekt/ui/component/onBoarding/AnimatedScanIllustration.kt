package com.example.yangdnashabschlussprojekt.ui.component.onBoarding

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedScanIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.25f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse), label = "scale"
    )
    val blurRadius by infiniteTransition.animateFloat(
        initialValue = 10f, targetValue = 25f,
        animationSpec = infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse), label = "blur"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Box(
            modifier = Modifier
                .size(130.dp)
                .scale(scale)
                .blur(blurRadius.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
        )
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 8.dp
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.padding(28.dp).fillMaxSize(),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}