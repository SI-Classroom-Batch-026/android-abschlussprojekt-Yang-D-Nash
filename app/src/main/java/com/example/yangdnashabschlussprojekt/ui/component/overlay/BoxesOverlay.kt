package com.example.yangdnashabschlussprojekt.ui.component.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.data.model.box.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel

@Composable
fun BoxesOverlay(
    viewModel: TextViewModel,
    arViewModel: ARViewModel,
    screenWidth: Int,
    screenHeight: Int
) {
    val textBoxes by viewModel.boundingBoxes.collectAsState()
    val arBoxes by arViewModel.boxes.collectAsState()

    Box(Modifier.fillMaxSize()) {

        (textBoxes + arBoxes).forEach { box ->
            AnimatedBoxView(
                box = box as TimedBoundingBox,
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
        }
    }
}

