package com.example.yangdnashabschlussprojekt.ui.component.onBoarding

import androidx.compose.animation.core.EaseInOutQuart
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun FocusIllustration() {
    val infiniteTransition = rememberInfiniteTransition(label = "focus")
    val lineMove by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutQuart), RepeatMode.Reverse), label = "move"
    )

    Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.TextSnippet,
            contentDescription = null,
            modifier = Modifier.size(70.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        )
        val color = MaterialTheme.colorScheme.primary
        Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
            val s = 5.dp.toPx()
            val l = 35.dp.toPx()
            val o = lineMove.dp.toPx()

            drawLine(color, Offset(o, o), Offset(o + l, o), s, StrokeCap.Round)
            drawLine(color, Offset(o, o), Offset(o, o + l), s, StrokeCap.Round)
            drawLine(color, Offset(size.width - o, o), Offset(size.width - o - l, o), s, StrokeCap.Round)
            drawLine(color, Offset(size.width - o, o), Offset(size.width - o, o + l), s, StrokeCap.Round)
            drawLine(color, Offset(o, size.height - o), Offset(o + l, size.height - o), s, StrokeCap.Round)
            drawLine(color, Offset(o, size.height - o), Offset(o, size.height - o - l), s, StrokeCap.Round)
            drawLine(color, Offset(size.width - o, size.height - o), Offset(size.width - o - l, size.height - o), s, StrokeCap.Round)
            drawLine(color, Offset(size.width - o, size.height - o), Offset(size.width - o, size.height - o - l), s, StrokeCap.Round)
        }
    }
}