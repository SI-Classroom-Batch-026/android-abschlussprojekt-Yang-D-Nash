package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    isTextMode: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }

    val analyzer = remember(textViewModel, arViewModel, isTextMode) {
        CameraXManager.FrameAnalyzer { imageProxy ->
            if (isTextMode) {
                textViewModel.analyzeImageProxy(imageProxy)
            } else {
                arViewModel.analyzeImageProxy(imageProxy)
            }
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)

    DisposableEffect(lifecycleOwner, cameraManager, analyzer) {

        cameraManager.startCamera(
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            analyzer = analyzer
        )

        onDispose {
            cameraManager.unbindAll()
        }
    }
}
