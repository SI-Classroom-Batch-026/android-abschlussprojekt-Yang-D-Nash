package com.example.yangdnashabschlussprojekt.ui.component.live

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
    val arBoxes by arViewModel.boundingBoxes.collectAsState()
    val textBoxes by textViewModel.boundingBoxes.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            cameraManager = cameraManager,
            textViewModel = textViewModel,
            arViewModel = arViewModel,
            modifier = Modifier.fillMaxSize(),
            isTextMode = !isObjectDetectionMode
        )
        BoxesOverlay(
            arViewModel = arViewModel,
            textViewModel = textViewModel,
            isTextMode = !isObjectDetectionMode,
        )
        if (detectedObjectLabel.isNotBlank() && !isCloudResult) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = detectedObjectLabel,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.Cyan,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}