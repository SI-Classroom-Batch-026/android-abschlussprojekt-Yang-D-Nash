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
    // screenWidth und screenHeight werden nicht mehr direkt benötigt,
    // da AnimatedBoxView BoxWithConstraints verwendet. Sie können aber bleiben.
    screenWidth: Int,
    screenHeight: Int
) {
    // Holt die Text-Boxen aus dem TextViewModel
    val textBoxes by viewModel.boundingBoxes.collectAsState()

    // Holt die AR-Boxen aus dem ARViewModel
    val arBoxes by arViewModel.boxes.collectAsState()

    // Holt die Frame-Größe (z.B. 1280x720) zur korrekten Skalierung
    val frameSize by viewModel.frameSize.collectAsState()

    // Kombiniert beide Listen zu einer einzigen Liste von TimedBoundingBox-Objekten
    val allBoxes: List<TimedBoundingBox> = (textBoxes + arBoxes)

    Box(Modifier.fillMaxSize()) {

        // NEU: Ruft AnimatedBoxView nur EINMAL mit der gesamten Liste auf
        AnimatedBoxView(
            boxes = allBoxes,
            frameSize = frameSize,
            modifier = Modifier.fillMaxSize()
        )
    }
}