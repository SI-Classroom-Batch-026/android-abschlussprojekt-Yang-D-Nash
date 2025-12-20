package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.camera.core.ImageProxy // WICHTIGER IMPORT
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun CameraPreview(
    cameraManager: CameraXManager,
    modifier: Modifier = Modifier,
    textViewModel: TextViewModel,
    arViewModel: ARViewModel,
    isTextMode: Boolean
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Typ explizit angeben: CameraXManager.FrameAnalyzer { imageProxy: ImageProxy -> ... }
    val analyzer = remember(isTextMode) {
        CameraXManager.FrameAnalyzer { imageProxy: ImageProxy ->
            if (isTextMode) {
                textViewModel.analyzeImageProxy(imageProxy)
            } else {
                arViewModel.analyzeImageProxy(imageProxy)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraManager.unbindAll() // Jetzt im Manager definiert
        }
    }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier,
        update = { previewView ->
            cameraManager.startCamera(
                previewView = previewView,
                lifecycleOwner = lifecycleOwner,
                analyzer = analyzer
            )
        }
    )
}