package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun CyberFocusFrame() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val s = size.width
            val len = 30.dp.toPx()
            val color = Color.Cyan.copy(0.4f)
            val stroke = 2.dp.toPx()

            // Vier Ecken zeichnen
            drawPath(Path().apply { moveTo(0f, len); lineTo(0f, 0f); lineTo(len, 0f) }, color, style = Stroke(stroke))
            drawPath(Path().apply { moveTo(s-len, 0f); lineTo(s, 0f); lineTo(s, len) }, color, style = Stroke(
                stroke
            )
            )
            drawPath(Path().apply { moveTo(0f, s-len); lineTo(0f, s); lineTo(len, s) }, color, style = Stroke(stroke))
            drawPath(Path().apply { moveTo(s-len, s); lineTo(s, s); lineTo(s, s-len) }, color, style = Stroke(stroke))
        }
    }
}