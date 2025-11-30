package com.example.yangdnashabschlussprojekt.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel

@Composable
fun BoxesOverlay(viewModel: ARViewModel) {
    val boxes by viewModel.boxes.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        boxes.forEach { box ->
            AnimatedBoxView(box = box)
        }
    }
}
