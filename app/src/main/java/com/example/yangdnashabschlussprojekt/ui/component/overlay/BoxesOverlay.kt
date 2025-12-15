package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.ui.component.camera.AnimatedBoxView // Wichtig: Neuer Import-Pfad
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun BoxesOverlay(
    viewModel: TextViewModel,
    arViewModel: ARViewModel,
) {
    val textBoxes by viewModel.boundingBoxes.collectAsState()

    val arBoxes by arViewModel.boxes.collectAsState()

    val frameSize by viewModel.frameSize.collectAsState()

    val allBoxes: List<TimedBoundingBox> = (textBoxes + arBoxes)

    Box(Modifier.fillMaxSize()) {

        AnimatedBoxView(
            boxes = allBoxes,
            frameSize = frameSize,
            modifier = Modifier.fillMaxSize()
        )
    }
}