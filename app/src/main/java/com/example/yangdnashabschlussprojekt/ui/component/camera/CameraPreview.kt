package com.example.yangdnashabschlussprojekt.ui.component.camera

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

    // Der Analyzer bleibt hier, da er das ImageProxy direkt an die ViewModels gibt
    val analyzer = remember(isTextMode) {
        CameraXManager.FrameAnalyzer { imageProxy ->
            if (isTextMode) {
                textViewModel.analyzeImageProxy(imageProxy)
            } else {
                arViewModel.analyzeImageProxy(imageProxy)
            }
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
            // WICHTIG: Nutze hier den analyzer-Parameter deiner startCamera Funktion
            cameraManager.startCamera(
                previewView = previewView,
                lifecycleOwner = lifecycleOwner,
                analyzer = analyzer // Hier wird der analyzer übergeben!
            )
        }
    )
}