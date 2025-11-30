package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.yangdnashabschlussprojekt.data.model.AnimatedBox

@Composable
fun AnimatedBoxItem(
    box: AnimatedBox,
    visible: Boolean
) {
    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f)

    val animatedLeft = animateFloatAsState(targetValue = box.left)
    val animatedTop = animateFloatAsState(targetValue = box.top)
    val animatedRight = animateFloatAsState(targetValue = box.right)
    val animatedBottom = animateFloatAsState(targetValue = box.bottom)

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            color = Color.Red.copy(alpha = alpha),
            topLeft = Offset(animatedLeft.value, animatedTop.value),
            size = Size(animatedRight.value - animatedLeft.value, animatedBottom.value - animatedTop.value),
            style = Stroke(width = 4f)
        )
    }
}
