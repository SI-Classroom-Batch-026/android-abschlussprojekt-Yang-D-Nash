package com.example.yangdnashabschlussprojekt.ui.component.live

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.overlay.BoxesOverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun CameraWithLiveObjects(
    cameraManager: CameraXManager,
    arViewModel: ARViewModel,
    textViewModel: TextViewModel,
) {
    var previewSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { previewSize = it.size }
    ) {
        CameraPreview(
            cameraManager = cameraManager,
            textViewModel = textViewModel,
            modifier = Modifier.fillMaxSize(),
            arViewModel = arViewModel
        )

        if (previewSize.width > 0 && previewSize.height > 0) {
            BoxesOverlay(
                viewModel = textViewModel,
                arViewModel = arViewModel,
                screenWidth = previewSize.width,
                screenHeight = previewSize.height
            )
        }
    }
}