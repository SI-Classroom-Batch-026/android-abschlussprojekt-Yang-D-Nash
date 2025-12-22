package com.example.yangdnashabschlussprojekt.ui.component.live

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    // Beobachte den Cloud-Status, um UI-Elemente auszublenden, wenn das Ergebnis da ist
    val isCloudResult by arViewModel.isCloudResult.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Die Kamera-Ebene
        CameraPreview(
            cameraManager = cameraManager,
            textViewModel = textViewModel,
            arViewModel = arViewModel,
            modifier = Modifier.fillMaxSize(),
            isTextMode = !isObjectDetectionMode
        )

        // 2. Die Kästen-Ebene (BoxesOverlay zeichnet die Rechtecke)
        // WICHTIG: Nur anzeigen, wenn wir nicht gerade ein Cloud-Ergebnis betrachten
        if (!isCloudResult) {
            BoxesOverlay(
                arViewModel = arViewModel,
                textViewModel = textViewModel,
                isTextMode = !isObjectDetectionMode,
            )
        }

        // 3. Live Label Badge (Cyber-Look)
        // Erscheint nur, wenn ML Kit etwas findet und wir nicht im Cloud-Modus sind
        if (detectedObjectLabel.isNotBlank() && !isCloudResult && !isCloudLoading) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 260.dp), // Höher gesetzt, damit es nicht über dem Button liegt
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