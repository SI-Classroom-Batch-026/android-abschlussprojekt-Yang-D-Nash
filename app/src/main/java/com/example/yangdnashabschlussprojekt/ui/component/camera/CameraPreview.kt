package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
    textViewModel: TextViewModel? = null,
    arViewModel: ARViewModel? = null,
    isTextMode: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    // Update den Modus im Manager, ohne die Kamera-Hardware neu zu starten
    LaunchedEffect(isTextMode) {
        cameraManager.isTextMode = isTextMode
    }

    // Erstelle den Analyzer nur einmal. Er entscheidet intern anhand des Flags,
    // welches ViewModel die Daten bekommt.
    val analyzer = remember {
        CameraXManager.FrameAnalyzer { imageProxy ->
            if (cameraManager.isTextMode) {
                textViewModel?.analyzeImageProxy(imageProxy)
            } else {
                arViewModel?.analyzeImageProxy(imageProxy)
            }
            // Falls deine ViewModels imageProxy.close() nicht selbst rufen,
            // müsste es hier stehen. Aber laut Standard-Pattern machen das die ViewModels.
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )

    // Kamera binden, wenn der Screen geladen wird
    DisposableEffect(lifecycleOwner) {
        cameraManager.startCamera(
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            analyzer = analyzer
        )
        onDispose {
            // Nur beim Verlassen des Screens unbinden
            cameraManager.unbindAll()
        }
    }
}