package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.overlay.AROverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel

@Composable
fun ARScreen(viewModel: ARViewModel) {
    val boxes by viewModel.boxes.collectAsState(initial = emptyList())

    var previewWidth by remember { mutableFloatStateOf(1f) }
    var previewHeight by remember { mutableFloatStateOf(1f) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            onPreviewSizeChanged = { width, height ->
                previewWidth = width
                previewHeight = height
            }
        )
        AROverlay(
            boxes = boxes,
            cameraWidth = previewWidth,
            cameraHeight = previewHeight,
            onBoxTap = { index -> viewModel.onBoxTapped(index) }
        )
    }
}
