package com.example.yangdnashabschlussprojekt.ui.screen

import android.graphics.Rect
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yangdnashabschlussprojekt.data.api.VisionResult
import com.example.yangdnashabschlussprojekt.data.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.util.image.saveTextAsFile
import com.example.yangdnashabschlussprojekt.util.scaleRect
import kotlinx.coroutines.CoroutineScope
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
    var cameraBitmapSize by remember { mutableStateOf(Pair(1, 1)) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)
            cameraManager.startCamera(previewView, ctx as androidx.lifecycle.LifecycleOwner)
            previewView
        }, modifier = Modifier.fillMaxSize())

        Canvas(modifier = Modifier.fillMaxSize()) {
            val (bitmapWidth, bitmapHeight) = cameraBitmapSize
            val viewWidth = size.width
            val viewHeight = size.height

            boundingBoxes.forEach { rect ->
                val scaledRect = scaleRect(rect, bitmapWidth, bitmapHeight, viewWidth.toInt(), viewHeight.toInt())
                drawRect(
                    color = Color.Yellow.copy(alpha = 0.3f),
                    topLeft = Offset(scaledRect.left.toFloat(), scaledRect.top.toFloat()),
                    size = Size(scaledRect.width().toFloat(), scaledRect.height().toFloat()),
                    style = Stroke(width = 3f)
                )
            }
        }

        Card(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 250.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            SelectionContainer {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Erkannter Text:", color = Color.White)
                    Text(recognizedText.ifBlank { "Noch kein Text erkannt" }, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Übersetzt:", color = Color.White)
                    Text(translatedText.ifBlank { "Noch keine Übersetzung" }, color = Color.White)
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(onClick = {
                cameraManager.captureFrame { bitmap ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val result: VisionResult = visionRepository.recognizeText(bitmap)
                        CoroutineScope(Dispatchers.Main).launch {
                            boundingBoxes = result.boxes
                            cameraBitmapSize = Pair(bitmap.width, bitmap.height)
                            viewModel.recognizeText(result.text)
                        }
                    }
                }
                Toast.makeText(context, "Text erkannt!", Toast.LENGTH_SHORT).show()
            }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Refresh, contentDescription = "OCR erneut", tint = Color.White)
                    Text("Scan", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }

            FloatingActionButton(onClick = { saveTextAsFile(context, recognizedText) }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Save, contentDescription = "Text speichern", tint = Color.White)
                    Text("Speichern", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
