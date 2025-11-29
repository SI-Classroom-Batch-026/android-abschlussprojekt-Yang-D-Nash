package com.example.yangdnashabschlussprojekt.ui.overlay

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

class AnimatedBox(
    val id: Int,
    val label: String,
    targetLeft: Float,
    targetTop: Float,
    targetRight: Float,
    targetBottom: Float
) {
    val animLeft = Animatable(targetLeft)
    val animTop = Animatable(targetTop)
    val animRight = Animatable(targetRight)
    val animBottom = Animatable(targetBottom)

    suspend fun updateTarget(left: Float, top: Float, right: Float, bottom: Float) {
        animLeft.animateTo(left, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        animTop.animateTo(top, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        animRight.animateTo(right, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        animBottom.animateTo(bottom, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
}
