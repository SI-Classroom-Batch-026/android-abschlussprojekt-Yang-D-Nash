package com.example.yangdnashabschlussprojekt.ui.component.live

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
@Composable
fun CameraWithLiveObjects(
    isTextMode: Boolean,
    onAnalyze: (ImageProxy) -> Unit,
    onCameraReady: (PreviewView, LifecycleOwner, ImageAnalysis.Analyzer) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        CameraPreview(
            isTextMode = isTextMode,
            onAnalyze = onAnalyze,
            onCameraReady = onCameraReady,
            modifier = Modifier.fillMaxSize()
        )
    }
}