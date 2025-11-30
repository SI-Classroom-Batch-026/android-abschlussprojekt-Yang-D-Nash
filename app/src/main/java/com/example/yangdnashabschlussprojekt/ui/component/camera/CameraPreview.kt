package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel

@Composable
fun CameraPreview(
    cameraManager: CameraXManager,
    modifier: Modifier = Modifier,
    arViewModel: ARViewModel
) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner
    val previewView = remember { PreviewView(context) }

    val analyzer = object : CameraXManager.FrameAnalyzer {
        @OptIn(ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val bitmap = imageProxy.image?.let { mediaImage ->
                // ⚠️ hier ggf. MediaImage → Bitmap konvertieren
                createBitmap(mediaImage.width, mediaImage.height)
            }
            bitmap?.let { analyzeFrame(it) }
            imageProxy.close()
        }

        override fun analyzeFrame(bitmap: Bitmap) {
            arViewModel.analyzeFrame(bitmap)
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)

    androidx.compose.runtime.LaunchedEffect(Unit) {
        cameraManager.startCamera(
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            analyzer = analyzer
        )
    }
}
