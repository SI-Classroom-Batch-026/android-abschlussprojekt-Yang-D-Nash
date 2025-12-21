package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    // Analyzer entscheidet basierend auf isTextMode, welches VM gerufen wird
    val analyzer = remember {
        ImageAnalysis.Analyzer { imageProxy ->
            if (cameraManager.isTextMode) {
                textViewModel.analyzeImageProxy(imageProxy)
            } else {
                arViewModel.analyzeImageProxy(imageProxy)
            }
        }
    }

    // Modus im Manager synchronisieren
    LaunchedEffect(isTextMode) {
        cameraManager.isTextMode = isTextMode
    }

    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                // Startet Kamera nur EINMAL beim Erstellen
                cameraManager.startCamera(this, lifecycleOwner, analyzer)
            }
        },
        modifier = modifier,
        update = { /* Keine Logik hier verhindert Ruckeln */ }
    )
}