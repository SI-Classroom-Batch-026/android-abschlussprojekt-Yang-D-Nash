package com.example.yangdnashabschlussprojekt.ui.screen

import android.graphics.Rect
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yangdnashabschlussprojekt.data.api.VisionResult
import com.example.yangdnashabschlussprojekt.data.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.BoundingBoxesCanvas
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.util.image.saveTextAsFile
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
    val recognizedText by viewModel.recognizedText.collectAsState()
    val translatedText by viewModel.translatedText.collectAsState()

    var boundingBoxes by remember { mutableStateOf(listOf<Rect>()) }
    var cameraBitmapSize by remember { mutableStateOf(1 to 1) }
    var isProcessing by remember { mutableStateOf(false) }
    var highlight by remember { mutableStateOf(false) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }
    val scope = rememberCoroutineScope()

    val vibrator = context.getSystemService(Vibrator::class.java)

    LaunchedEffect(highlight) {
        if (highlight) {
            vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            kotlinx.coroutines.delay(500)
            highlight = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // Camera preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraManager.startCamera(previewView, ctx as androidx.lifecycle.LifecycleOwner)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Pulse for bounding boxes
        val infiniteTransition = rememberInfiniteTransition()
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(1000))
        )

        BoundingBoxesCanvas(
            boundingBoxes = boundingBoxes,
            bitmapSize = cameraBitmapSize,
            highlight = highlight,
            pulseAlpha = pulseAlpha
        )


        BottomTextCard(
            recognizedText = recognizedText,
            translatedText = translatedText,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        TextScreenFABs(
            onScanClick = {
                isProcessing = true
                cameraManager.captureFrame { bitmap, rotation ->
                    scope.launch(Dispatchers.IO) {
                        val result: VisionResult = visionRepository.recognizeText(bitmap)
                        launch(Dispatchers.Main) {
                            boundingBoxes = result.boxes
                            cameraBitmapSize = bitmap.width to bitmap.height
                            viewModel.recognizeText(result.text)
                            highlight = true
                            Toast.makeText(context, "Text erkannt!", Toast.LENGTH_SHORT).show()
                            isProcessing = false
                        }
                    }
                }
            },
            onSaveClick = { saveTextAsFile(context, recognizedText) },
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
