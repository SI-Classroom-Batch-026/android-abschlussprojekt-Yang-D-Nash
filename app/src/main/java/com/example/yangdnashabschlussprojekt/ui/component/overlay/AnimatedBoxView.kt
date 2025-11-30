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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.yangdnashabschlussprojekt.data.model.box.TimedBoundingBox
@Composable
fun AnimatedBoxView(box: TimedBoundingBox, screenWidth: Int, screenHeight: Int) {

    val alpha by animateFloatAsState(targetValue = 1f, animationSpec = tween(300))

    val left by animateDpAsState(
        targetValue = (box.left / box.frameWidth) * screenWidth.dp
    )
    val top by animateDpAsState(
        targetValue = (box.top / box.frameHeight) * screenHeight.dp
    )
    val width by animateDpAsState(
        targetValue = ((box.right - box.left) / box.frameWidth) * screenWidth.dp
    )
    val height by animateDpAsState(
        targetValue = ((box.bottom - box.top) / box.frameHeight) * screenHeight.dp
    )

    Box(
        modifier = Modifier
            .offset(x = left, y = top)
            .size(width, height)
            .alpha(alpha)
            .border(2.dp, box.color)
    ) {
        Text(
            text = box.label,
            color = Color.White
        )
    }
}
