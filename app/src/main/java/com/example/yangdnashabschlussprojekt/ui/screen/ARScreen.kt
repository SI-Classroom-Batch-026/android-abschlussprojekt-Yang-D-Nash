package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.overlay.AROverlay
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ARScreen(
    viewModel: ARViewModel = koinViewModel()
) {
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }

    val boxes by viewModel.boxes.collectAsState()

    val analyzer = remember {
        ImageAnalysis.Analyzer { imageProxy ->
            viewModel.analyzeFrame(imageProxy)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(
            lifecycleOwner = lifecycleOwner,
            previewView = previewView,
            analyzer = analyzer
        )

        AROverlay(boxes)
    }
}
