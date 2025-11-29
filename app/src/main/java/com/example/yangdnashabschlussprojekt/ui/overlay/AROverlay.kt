package com.example.yangdnashabschlussprojekt.ui.overlay

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AROverlay(
    boxes: List<AnimatedBox>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context -> AnimatedOverlayView(context) },
        update = { view ->
            view.updateBoxes(boxes)
        },
        modifier = modifier
    )
}

private class AnimatedOverlayView(context: Context) : View(context) {

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }

    private var boxes: List<AnimatedBox> = emptyList()
    private val scope = CoroutineScope(Dispatchers.Main)

    fun updateBoxes(newBoxes: List<AnimatedBox>) {
        // Update the current boxes
        boxes = newBoxes

        // Launch animations to target positions
        scope.launch {
            boxes.forEach { box ->
                box.updateTarget(box.animLeft.value, box.animTop.value, box.animRight.value, box.animBottom.value)
            }
        }

        invalidate() // force redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (box in boxes) {
            canvas.drawRect(
                box.animLeft.value,
                box.animTop.value,
                box.animRight.value,
                box.animBottom.value,
                paint
            )
        }
    }
}
