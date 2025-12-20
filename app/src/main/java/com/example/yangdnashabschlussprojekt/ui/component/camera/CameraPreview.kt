package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
    textViewModel: TextViewModel? = null,
    arViewModel: ARViewModel? = null,
    isTextMode: Boolean = true
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // WICHTIG: Der Analyzer muss stabil sein und den Frame sofort schließen!
    val analyzer = remember(isTextMode) {
        CameraXManager.FrameAnalyzer { imageProxy ->
            if (isTextMode) {
                textViewModel?.analyzeImageProxy(imageProxy)
            } else {
                arViewModel?.analyzeImageProxy(imageProxy)
            }
            // Falls das ViewModel das ImageProxy nicht schließt,
            // bleibt der Stream hier hängen!
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