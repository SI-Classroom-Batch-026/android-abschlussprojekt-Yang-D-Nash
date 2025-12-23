package com.example.yangdnashabschlussprojekt.ui.component.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yangdnashabschlussprojekt.ui.component.onBoarding.FocusIllustration
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

    val analyzer = remember {
        ImageAnalysis.Analyzer { imageProxy ->
            if (cameraManager.isTextMode) {
                textViewModel.analyzeImageProxy(imageProxy)
            } else {
                arViewModel.analyzeImageProxy(imageProxy)
            }
        }
    }
    LaunchedEffect(isTextMode) {
        cameraManager.isTextMode = isTextMode
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        @Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    cameraManager.startCamera(this, lifecycleOwner, analyzer)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(28.dp))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        )
        if (isTextMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                FocusIllustration()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                )
            }
        }
    }
}