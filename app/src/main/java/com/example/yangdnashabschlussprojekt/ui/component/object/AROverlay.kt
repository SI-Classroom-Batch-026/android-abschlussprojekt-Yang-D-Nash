package com.example.yangdnashabschlussprojekt.ui.component.`object`

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.yangdnashabschlussprojekt.data.graphics.AnimatedBox
import kotlinx.coroutines.launch

@Composable
fun AROverlay(
    modifier: Modifier = Modifier,
    animatedBoxes: List<AnimatedBox>
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            animatedBoxes.forEach { box ->
                // Animation auf Boxen
                scope.launch {
                    box.animLeft.animateTo(box.animLeft.value) // hier kannst du targets setzen
                    box.animTop.animateTo(box.animTop.value)
                    box.animRight.animateTo(box.animRight.value)
                    box.animBottom.animateTo(box.animBottom.value)
                }

                // Rechteck zeichnen
                drawRect(
                    color = Color.Red,
                    topLeft = Offset(box.animLeft.value, box.animTop.value),
                    size = Size(
                        box.animRight.value - box.animLeft.value,
                        box.animBottom.value - box.animTop.value
                    ),
                    style = Stroke(width = 3f)
                )

                // Label zeichnen
                drawContext.canvas.nativeCanvas.apply {
                    drawText(
                        box.label,
                        box.animLeft.value,
                        box.animTop.value - 8f,
                        Paint().apply {
                            color = android.graphics.Color.RED
                            textSize = 40f
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
}
