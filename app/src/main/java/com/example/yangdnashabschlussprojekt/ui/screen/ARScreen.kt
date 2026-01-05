package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.ui.component.camera.HoldToScanButton
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ARResultOverlay
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ScanningLaserOverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
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
    val boxes by arViewModel.boundingBoxes.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        CameraWithLiveObjects(
            isObjectDetectionMode = true,
            onAnalyze = {  },
            onCameraReady = { previewView, lifecycleOwner, _ ->
                cameraManager.startCamera(previewView, lifecycleOwner, arViewModel)
            }
        )
        ARResultOverlay(boxes = boxes)
        if (isCloudLoading) ScanningLaserOverlay(Color(0xFF00FFCC))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            if (label.isNotEmpty()) {
                Surface(color = Color.Black.copy(0.7f), shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.padding(bottom = 120.dp)) {
                if (!isCloudResult && !isCloudLoading) {
                    HoldToScanButton(onTrigger = {
                        triggerVibration(context)
                        cameraManager.captureForCloudScan(
                            onCaptured = { arViewModel.analyzeWithCloudVision(it) },
                            onError = {  }
                        )
                    })
                } else if (isCloudResult) {
                    Button(
                        onClick = { arViewModel.resetCloudResult() },
                        modifier = Modifier.height(60.dp).fillMaxWidth(0.7f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC))
                    ) {
                        Text(
                            text = "SYSTEM RESET",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}