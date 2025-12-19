package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.runtime.Composable

@Composable
fun AnimatedFab(
    isVisible: Boolean,
    delay: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { 50 },
            animationSpec = tween(durationMillis = 400, delayMillis = delay)
        ) + fadeIn(animationSpec = tween(400, delayMillis = delay))
    ) {
        content()
    }
}
