package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.component.`object`.AROverlay
import com.example.yangdnashabschlussprojekt.ui.component.`object`.rememberAnimatedBoxes
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel


@Composable
fun ARScreen(viewModel: ARViewModel) {
    val detectedObjects by viewModel.detectedObjects.collectAsState()
    val animatedBoxes = rememberAnimatedBoxes(detectedObjects)

    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            onPreviewSizeChanged = { width, height -> /* optional für Skalierung */ }
        )

        AROverlay(
            modifier = Modifier.fillMaxSize(),
            animatedBoxes = animatedBoxes
        )
    }
}

