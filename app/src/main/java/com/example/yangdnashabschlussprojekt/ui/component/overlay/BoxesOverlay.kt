package com.example.yangdnashabschlussprojekt.ui.component.overlay

import android.util.Size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
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

    val currentFrameSize: Size = if (isTextMode) {
        textFrameSize
    } else {
        arFrameSize
    }

    val allBoxes: List<TimedBoundingBox> = (textBoxes + arBoxes)

    Box(Modifier.fillMaxSize()) {
        if (currentFrameSize.width > 0 && currentFrameSize.height > 0) {
            AnimatedBoxView(
                boxes = allBoxes,
                frameSize = currentFrameSize,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}