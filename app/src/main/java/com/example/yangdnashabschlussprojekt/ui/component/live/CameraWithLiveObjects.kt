package com.example.yangdnashabschlussprojekt.ui.component.live

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.overlay.BoxesOverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun CameraWithLiveObjects(
    cameraManager: CameraXManager,
    arViewModel: ARViewModel,
    textViewModel: TextViewModel,
    isObjectDetectionMode: Boolean,
    detectedObjectLabel: String,
    detectedTextLabel: String
) {
    var previewSize by remember { mutableStateOf(IntSize.Zero) }

    val isTextMode = remember(isObjectDetectionMode) {
        !isObjectDetectionMode
    }

    val currentLabel = if (isObjectDetectionMode) detectedObjectLabel else detectedTextLabel


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
            isTextMode = isTextMode
        )
        if (previewSize.width > 0 && previewSize.height > 0) {
            BoxesOverlay(
                arViewModel = arViewModel,
                textViewModel = textViewModel,
                isTextMode = isTextMode,
            )
        }

        if (currentLabel.isNotBlank()) {
            Text(
                text = currentLabel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.6f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(8.dp),
                color = Color.White
            )
        }
    }
}