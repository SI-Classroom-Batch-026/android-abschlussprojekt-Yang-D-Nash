package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.overlay.BoxesOverlay
import com.example.yangdnashabschlussprojekt.ui.component.text.BoundingBoxesCanvas
import com.example.yangdnashabschlussprojekt.ui.component.text.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

@Composable
fun ARScreen(viewModel: ARViewModel = koinViewModel()) {

    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }

    var timedBoundingBoxes by remember { mutableStateOf(listOf<TimedBoundingBox>()) }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(
            cameraManager = cameraManager,
            modifier = Modifier.fillMaxSize(),
            onBoundingBoxes = { boxes ->
                val now = System.currentTimeMillis()
                timedBoundingBoxes = boxes.map { rect ->
                    TimedBoundingBox(rect = rect, timestamp = now)
                }
            }
        )

        BoundingBoxesCanvas(boundingBoxes = timedBoundingBoxes)

        BoxesOverlay(viewModel = viewModel)
    }
}
