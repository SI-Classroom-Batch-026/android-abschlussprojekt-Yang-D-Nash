package com.example.yangdnashabschlussprojekt.ui.overlay

import android.graphics.Rect
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring


class AnimatedBox(
    val id: Int,
    val label: String,
    left: Float,
    top: Float,
    right: Float,
    bottom: Float
) {
    val animLeft = Animatable(left)
    val animTop = Animatable(top)
    val animRight = Animatable(right)
    val animBottom = Animatable(bottom)

    suspend fun updateTarget(left: Float, top: Float, right: Float, bottom: Float) {
        animLeft.animateTo(left, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        animTop.animateTo(top, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        animRight.animateTo(right, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        animBottom.animateTo(bottom, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }

    companion object {
        fun fromRect(id: Int, rect: Rect, label: String) = AnimatedBox(
            id,
            label,
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat()
        )
    }
}

