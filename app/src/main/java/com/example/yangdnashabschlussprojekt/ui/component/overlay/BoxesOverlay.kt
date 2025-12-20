package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.ui.component.camera.AnimatedBoxView
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun BoxesOverlay(
    textViewModel: TextViewModel,
    arViewModel: ARViewModel,
    isTextMode: Boolean
) {
    val textBoxes by textViewModel.boundingBoxes.collectAsState()
    val arBoxes by arViewModel.boundingBoxes.collectAsState()
    val textFrameSize by textViewModel.frameSize.collectAsState()
    val arFrameSize by arViewModel.frameSize.collectAsState()

    val activeBoxes = if (isTextMode) textBoxes else arBoxes
    val activeFrameSize = if (isTextMode) textFrameSize else arFrameSize

    if (activeFrameSize.width > 0 && activeFrameSize.height > 0) {
        AnimatedBoxView(
            boxes = activeBoxes,
            frameSize = activeFrameSize,
            isTextMode = isTextMode,
            modifier = Modifier.fillMaxSize()
        )
    }
}