package com.example.yangdnashabschlussprojekt.ui.screen

import android.graphics.Rect
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraPreview
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.BoundingBoxesCanvas
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
import com.example.yangdnashabschlussprojekt.ui.component.text.TimedBoundingBox
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

@Composable
fun TextScreen(
    viewModel: TextViewModel = koinViewModel(),
    visionRepository: VisionRepository
) {
    val context = LocalContext.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }
    val scope = rememberCoroutineScope()
    val vibrator = context.getSystemService(Vibrator::class.java)

    var boundingBoxes by remember { mutableStateOf(listOf<Rect>()) }
    var isProcessing by remember { mutableStateOf(false) }
    var highlight by remember { mutableStateOf(false) }

    LaunchedEffect(highlight) {
        if (highlight) {
            vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            kotlinx.coroutines.delay(500)
            highlight = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(
            cameraManager = cameraManager,
            modifier = Modifier.fillMaxSize(),
            onBoundingBoxes = { boxes -> boundingBoxes = boxes }
        )

        BoundingBoxesCanvas(
            boundingBoxes = boundingBoxes.map { rect ->
                TimedBoundingBox(
                    rect = rect,
                    timestamp = System.currentTimeMillis(),
                    color = Color.Magenta,
                    bitmapWidth = 1280,
                    bitmapHeight = 720
                )
            }
        )



        BottomTextCard(
            recognizedText = viewModel.recognizedText.collectAsState().value,
            translatedText = viewModel.translatedText.collectAsState().value,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        TextScreenFABs(
            onScanClick = {
                isProcessing = true
                cameraManager.captureFrame { bitmap, _ ->
                    scope.launch(Dispatchers.IO) {
                        val result = visionRepository.recognizeText(bitmap)
                        launch(Dispatchers.Main) {
                            boundingBoxes = result.boxes
                            viewModel.recognizeText(result.text)
                            highlight = true
                            Toast.makeText(context, "Text erkannt!", Toast.LENGTH_SHORT).show()
                            isProcessing = false
                        }
                    }
                }
            },
            onSaveClick = { },
            modifier = Modifier.align(Alignment.BottomEnd)
        )

        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.Cyan)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Text wird erkannt...", color = Color.White)
                }
            }
        }
    }
}
