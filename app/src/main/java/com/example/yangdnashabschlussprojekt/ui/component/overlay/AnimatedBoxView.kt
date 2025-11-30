package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.model.AnimatedBox

@Composable
fun AnimatedBoxView(box: AnimatedBox) {
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500)
    )

    val left by animateDpAsState(targetValue = box.left.dp)
    val top by animateDpAsState(targetValue = box.top.dp)
    val width by animateDpAsState(targetValue = (box.right - box.left).dp)
    val height by animateDpAsState(targetValue = (box.bottom - box.top).dp)

    Box(
        modifier = Modifier
            .offset(x = left, y = top)
            .size(width, height)
            .alpha(alpha)
            .border(2.dp, Color.Red)
    ) {
        Text(
            text = box.label,
            color = Color.White,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}
