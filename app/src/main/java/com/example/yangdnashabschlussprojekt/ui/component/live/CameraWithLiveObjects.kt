package com.example.yangdnashabschlussprojekt.ui.component.live

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.component.overlay.BoxesOverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun CameraWithLiveObjects(
    cameraManager: CameraXManager,
    arViewModel: ARViewModel,
    textViewModel: TextViewModel,
    isObjectDetectionMode: Boolean,
    detectedObjectLabel: String,
) {
    val isCloudResult by arViewModel.isCloudResult.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()

    // Pulsieren-Animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = if (isCloudResult) 0.5f else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            cameraManager = cameraManager,
            textViewModel = textViewModel,
            arViewModel = arViewModel,
            modifier = Modifier.fillMaxSize(),
            isTextMode = !isObjectDetectionMode
        )

        // Overlay IMMER anzeigen, aber bei Cloud-Result pulsieren lassen
        Box(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = pulseAlpha)) {
            BoxesOverlay(
                arViewModel = arViewModel,
                textViewModel = textViewModel,
                isTextMode = !isObjectDetectionMode,
            )
        }

        // Live Badge (nur wenn nicht Cloud aktiv ist)
        if (detectedObjectLabel.isNotBlank() && !isCloudResult && !isCloudLoading) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 260.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "LIVE: ${detectedObjectLabel.uppercase()}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Cyan,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}