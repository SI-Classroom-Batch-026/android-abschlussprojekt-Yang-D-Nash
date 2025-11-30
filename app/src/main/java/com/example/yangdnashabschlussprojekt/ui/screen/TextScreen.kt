package com.example.yangdnashabschlussprojekt.ui.screen

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraWithBoundingBoxes
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

@Composable
fun TextScreen(
    textViewModel: TextViewModel = koinViewModel(),
    arViewModel: ARViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }
    val vibrator = context.getSystemService(Vibrator::class.java)

    var highlight by remember { mutableStateOf(false) }

    LaunchedEffect(highlight) {
        if (highlight) {
            vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            highlight = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraWithBoundingBoxes(
            cameraManager = cameraManager,
            textViewModel = koinViewModel(),
            arViewModel = koinViewModel()
        )

        BottomTextCard(
            recognizedText = textViewModel.recognizedText.collectAsState().value,
            translatedText = textViewModel.translatedText.collectAsState().value,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        TextScreenFABs(
            onScanClick = {
                cameraManager.captureFrame { bitmap, _ ->
                    textViewModel.analyzeFrame(bitmap)
                    highlight = true
                }
            },
            onSaveClick = { /* TODO: Speichern */ },
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
