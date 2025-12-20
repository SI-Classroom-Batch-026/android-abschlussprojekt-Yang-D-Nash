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
            // Wichtig: onGloballyPositioned muss auf dem Container sitzen,
            // der die exakte Größe der Kamera-Fläche hat
            .onGloballyPositioned { coordinates ->
                previewSize = coordinates.size
            }
    ) {
        // 1. Hintergrund: Kamera
        CameraPreview(
            cameraManager = cameraManager,
            textViewModel = textViewModel,
            arViewModel = arViewModel,
            modifier = Modifier.fillMaxSize(),
            isTextMode = !isObjectDetectionMode
        )

        // 2. Mittlere Ebene: Die Boxen (Z-Index beachten!)
        if (previewSize.width > 0 && previewSize.height > 0) {
            // Wir packen das Overlay in eine Box mit matchParentSize,
            // damit es exakt auf der Kamera liegt
            Box(modifier = Modifier.matchParentSize()) {
                BoxesOverlay(
                    arViewModel = arViewModel,
                    textViewModel = textViewModel,
                    isTextMode = !isObjectDetectionMode,
                    // Gib die Größe explizit mit, falls dein Overlay sie braucht
                )
            }
        }

        // 3. Obere Ebene: Label
        if (detectedObjectLabel.isNotBlank() && !isCloudResult) {
            Text(
                text = detectedObjectLabel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                color = Color.White
            )
        }
    }
}