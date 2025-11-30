package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.graphics.Rect
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yangdnashabschlussprojekt.ui.component.text.TextAnalyzer

@Composable
fun CameraPreview(
    cameraManager: CameraXManager,
    modifier: Modifier = Modifier,
    onBoundingBoxes: (List<Rect>) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = context as androidx.lifecycle.LifecycleOwner
    val previewView = remember { PreviewView(context) }

    val analyzer = remember {
        TextAnalyzer { boxes -> onBoundingBoxes(boxes) }
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
