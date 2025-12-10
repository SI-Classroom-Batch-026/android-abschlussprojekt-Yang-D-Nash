package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun CameraPreview(
    cameraManager: CameraXManager,
    modifier: Modifier = Modifier,
    textViewModel: TextViewModel,
    arViewModel: ARViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner
    val previewView = remember { PreviewView(context) }

    // LAMBDA-VERSION: Kompakt und modern (SAM Interface)
    val analyzer = CameraXManager.FrameAnalyzer { imageProxy ->

        @OptIn(ExperimentalGetImage::class)
        // LÖSUNG: Aufruf der toBitmap-Funktion über die Instanz cameraManager
        val bitmap = cameraManager.toBitmap(imageProxy)

        bitmap?.let {
            textViewModel.analyzeFrame(it)
        }

        imageProxy.close()
    }

    AndroidView(factory = { previewView }, modifier = modifier)

    LaunchedEffect(Unit) {
        cameraManager.startCamera(
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            analyzer = analyzer
        )
    }
}