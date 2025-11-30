package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

@Composable
fun ARScreen(
    viewModel: ARViewModel = koinViewModel(),
    textViewModel: TextViewModel = koinViewModel()
){
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraWithLiveObjects(
            cameraManager = cameraManager,
            arViewModel = viewModel,
            textViewModel = textViewModel
        )
    }
}

