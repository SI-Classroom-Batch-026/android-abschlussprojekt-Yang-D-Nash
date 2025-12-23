package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yangdnashabschlussprojekt.ui.component.camera.HoldToScanButton
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ScanningLaserOverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ARScreen(
    arViewModel: ARViewModel = koinViewModel(),
    cameraManager: CameraXManager = koinInject()
) {
    val context = LocalContext.current
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()
    val label by arViewModel.detectedObjectLabel.collectAsState()

    // Hält die Cloud-Boxen im Overlay am Leben
    LaunchedEffect(isCloudResult) {
        while (isCloudResult) {
            arViewModel.refreshCloudBoxes()
            delay(100)
        }
    }

    Scaffold(containerColor = Color.Black) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            CameraWithLiveObjects(
                cameraManager = cameraManager,
                arViewModel = arViewModel,
                textViewModel = koinViewModel(),
                isObjectDetectionMode = true,
                detectedObjectLabel = label
            )

            if (isCloudLoading) {
                ScanningLaserOverlay(laserColor = Color.Cyan)
            }

            // UI Layer
            Box(Modifier.fillMaxSize().zIndex(10f)) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 140.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (!isCloudResult && !isCloudLoading) {
                        HoldToScanButton(onTrigger = {
                            triggerVibration(context)
                            cameraManager.captureForCloudScan(
                                onCaptured = { arViewModel.analyzeWithCloudVision(it) },
                                onError = { }
                            )
                        })
                    }

                    if (isCloudResult) {
                        Button(
                            onClick = { arViewModel.resetCloudResult() },
                            modifier = Modifier.height(60.dp).fillMaxWidth(0.7f),
                            shape = RoundedCornerShape(30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC))
                        ) {
                            Text("NEUER SCAN", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}