package com.example.yangdnashabschlussprojekt.ui.component.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
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
    var previewSize by remember { mutableStateOf(IntSize.Zero) }
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { previewSize = it.size }
    ) {
        CameraPreview(
            cameraManager = cameraManager,
            textViewModel = textViewModel,
            arViewModel = arViewModel,
            modifier = Modifier.fillMaxSize(),
            isTextMode = !isObjectDetectionMode
        )

        // Die Boxen-Ebene
        if (previewSize.width > 0 && previewSize.height > 0) {
            BoxesOverlay(
                arViewModel = arViewModel,
                textViewModel = textViewModel,
                isTextMode = !isObjectDetectionMode,
            )
        }

        // Live-Label unten (nur zeigen, wenn kein Cloud-Ergebnis oben klebt)
        if (detectedObjectLabel.isNotBlank() && !isCloudResult) {
            Text(
                text = detectedObjectLabel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                    .padding(8.dp),
                color = Color.White
            )
        }
    }
}