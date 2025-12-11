package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
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
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    val analyzer = CameraXManager.FrameAnalyzer { imageProxy ->

        @OptIn(ExperimentalGetImage::class)
        val bitmap = cameraManager.toBitmap(imageProxy)

        try {
            bitmap?.let {
                textViewModel.analyzeFrame(it)
            }
        } catch (e: Exception) {
            Log.e("CameraPreview", "Fehler bei der Frame-Analyse", e)
        } finally {
            imageProxy.close()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)

    DisposableEffect(lifecycleOwner) {
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