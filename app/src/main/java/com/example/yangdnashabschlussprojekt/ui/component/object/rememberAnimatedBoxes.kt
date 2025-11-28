package com.example.yangdnashabschlussprojekt.ui.component.`object`

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.yangdnashabschlussprojekt.data.graphics.AnimatedBox
import com.example.yangdnashabschlussprojekt.data.graphics.DetectedObject

@Composable
fun rememberAnimatedBoxes(
    detectedObjects: List<DetectedObject>
): List<AnimatedBox> {
    return detectedObjects.map { obj ->
        AnimatedBox(
            label = obj.label,
            animLeft = remember { Animatable(obj.boundingBox.left.toFloat()) },
            animTop = remember { Animatable(obj.boundingBox.top.toFloat()) },
            animRight = remember { Animatable(obj.boundingBox.right.toFloat()) },
            animBottom = remember { Animatable(obj.boundingBox.bottom.toFloat()) },
            isNew = true
        )
    }
}
