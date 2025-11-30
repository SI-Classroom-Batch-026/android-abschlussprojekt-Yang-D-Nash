package com.example.yangdnashabschlussprojekt.ui.component.text

import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.yangdnashabschlussprojekt.util.camera.scaleRectToView

/**
 * Map API Landscape-box to Portrait-box coordinates
 */
fun mapLandscapeToPortrait(rect: Rect, bitmapWidth: Int, bitmapHeight: Int): Rect {
    return Rect(
        rect.top,                    // new left
        bitmapWidth - rect.right,    // new top
        rect.bottom,                 // new right
        bitmapWidth - rect.left      // new bottom
    )
}

@Composable
fun BoundingBoxesCanvas(
    boundingBoxes: List<Rect>,
    bitmapSize: Pair<Int, Int>,
    highlight: Boolean,
    pulseAlpha: Float
) {
    val (bitmapWidth, bitmapHeight) = bitmapSize

    Canvas(modifier = Modifier.fillMaxSize()) {
        val viewWidth = size.width.toInt()
        val viewHeight = size.height.toInt()

        boundingBoxes.forEach { rect ->
            // Map Landscape -> Portrait
            val portraitRect = mapLandscapeToPortrait(rect, bitmapWidth, bitmapHeight)
            // Scale to view coordinates
            val scaled = scaleRectToView(portraitRect, bitmapHeight, bitmapWidth, viewWidth, viewHeight)

            // Glow effect (3 layers)
            for (i in 3 downTo 1) {
                drawRoundRect(
                    color = Color.Cyan.copy(alpha = 0.12f * i),
                    topLeft = Offset(scaled.left.toFloat() - i, scaled.top.toFloat() - i),
                    size = Size(
                        scaled.width().toFloat() + 2 * i,
                        scaled.height().toFloat() + 2 * i
                    ),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 2f)
                )
            }

            // Main rectangle (pulse + highlight)
            drawRoundRect(
                color = if (highlight) Color.Magenta.copy(alpha = 0.9f)
                else Color.Cyan.copy(alpha = pulseAlpha),
                topLeft = Offset(scaled.left.toFloat(), scaled.top.toFloat()),
                size = Size(scaled.width().toFloat(), scaled.height().toFloat()),
                cornerRadius = CornerRadius(12f, 12f),
                style = Stroke(width = 3f)
            )
        }
    }
}
