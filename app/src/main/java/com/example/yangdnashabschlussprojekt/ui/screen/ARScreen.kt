package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.camera.HoldToScanButton
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ARResultOverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ARScreen(
    arViewModel: ARViewModel = koinViewModel(),
    cameraManager: CameraXManager = koinInject()
) {
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    Scaffold(containerColor = Color.Transparent) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            CameraWithLiveObjects(
                cameraManager = cameraManager,
                arViewModel = arViewModel,
                isObjectDetectionMode = true,
                detectedObjectLabel = detectedObjectLabel,
                textViewModel = koinViewModel ()
            )

            // LIVE FEEDBACK: Kleiner Text über dem Button
            if (detectedObjectLabel.isNotBlank() && !isCloudResult && !isCloudLoading) {
                Text(
                    text = detectedObjectLabel.uppercase(),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 240.dp) // Über dem Lens Button
                        .background(Color.Cyan.copy(0.7f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Lens Scan Button
            if (!isCloudResult && !isCloudLoading) {
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 150.dp)) {
                    HoldToScanButton(onTrigger = {
                        cameraManager.captureForCloudScan(
                            onCaptured = { arViewModel.analyzeWithCloudVision(it) },
                            onError = { }
                        )
                    })
                }
            }

            // Cloud Overlay
            if (isCloudResult) {
                ARResultOverlay(label = detectedObjectLabel, onReset = { arViewModel.resetCloudResult() })
            }
        }
    }
}