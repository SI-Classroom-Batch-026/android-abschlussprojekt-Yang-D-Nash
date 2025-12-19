package com.example.yangdnashabschlussprojekt.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

@Composable
fun FocusIllustration(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "focus")

    // 1. Stärkere Größen-Animation (0.7 bis 1.1)
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing), // Schneller & geschmeidiger
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // 2. Transparenz-Animation (Pulsieren)
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(200.dp), // Etwas größer
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.TextSnippet,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        val color = MaterialTheme.colorScheme.primary

        Canvas(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            val stroke = 10.dp.toPx() // Dickere Linien
            val length = 50.dp.toPx() * scale // Deutlichere Längenänderung

            // Wir nutzen hier das animierte Alpha für die Farbe
            val pulseColor = color.copy(alpha = alpha)

            // Oben Links
            drawLine(pulseColor, Offset(0f, 0f), Offset(length, 0F) , stroke)
            drawLine(pulseColor, Offset(0f, 0f), Offset(0F, length), stroke)

            // Oben Rechts
            drawLine(pulseColor, Offset(size.width, 0f), Offset(size.width - length, 0f), stroke)
            drawLine(pulseColor, Offset(size.width, 0f), Offset(0f + size.width, length), stroke)

            // Unten Links
            drawLine(pulseColor, Offset(0f, size.height), Offset(length, size.height), stroke)
            drawLine(pulseColor, Offset(0f, size.height), Offset(0f, size.height - length), stroke)

            // Unten Rechts
            drawLine(pulseColor, Offset(size.width, size.height), Offset(size.width - length, size.height), stroke)
            drawLine(pulseColor, Offset(size.width, size.height), Offset(size.width, size.height - length), stroke)
        }
    }
}