package com.example.yangdnashabschlussprojekt.ui.component.camera.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun FullScreenScannerOverlay() {
    Box(Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val sW = size.width
            val sH = size.height
            val cornerLen = 30.dp.toPx()
            val color = Color.Cyan.copy(alpha = 0.4f)
            val strokeWidth = 1.5.dp.toPx()

            drawPath(Path().apply {
                moveTo(0f, cornerLen); lineTo(0f, 0f); lineTo(cornerLen, 0f) 
            }, color, style = Stroke(strokeWidth))

            drawPath(Path().apply {
                moveTo(sW - cornerLen, 0f); lineTo(sW, 0f); lineTo(sW, cornerLen) 
            }, color, style = Stroke(strokeWidth))

            drawPath(Path().apply {
                moveTo(0f, sH - cornerLen); lineTo(0f, sH); lineTo(cornerLen, sH) 
            }, color, style = Stroke(strokeWidth))

            drawPath(Path().apply {
                moveTo(sW - cornerLen, sH); lineTo(sW, sH); lineTo(sW, sH - cornerLen) 
            }, color, style = Stroke(strokeWidth))
        }
    }
}