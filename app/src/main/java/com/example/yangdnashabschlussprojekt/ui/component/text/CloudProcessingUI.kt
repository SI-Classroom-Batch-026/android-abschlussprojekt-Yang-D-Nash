package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ScanningLaserOverlay

@Composable
fun CloudProcessingUI() {
    Box(Modifier.fillMaxSize().background(Color.Black.copy(0.8f)).zIndex(100f), Alignment.Center) {
        ScanningLaserOverlay(laserColor = Color.Magenta)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color.Magenta, strokeWidth = 2.dp)
            Spacer(Modifier.height(24.dp))
            Text(
                "EXTRACTING DATA...",
                color = Color.Magenta,
                fontWeight = FontWeight.Light,
                letterSpacing = 4.sp
            )
        }
    }
}