package com.example.yangdnashabschlussprojekt.ui.screen

import android.graphics.Rect
import android.widget.Toast
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yangdnashabschlussprojekt.data.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.util.image.saveTextAsFile
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
    var cameraBitmapSize by remember { mutableStateOf(Pair(1, 1)) } // width, height

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)
            val manager = CameraXManager(
                ctx,
                cameraExecutor,
                visionRepository
            ) { result ->
                boundingBoxes = result.boxes
                cameraBitmapSize = Pair(result.width, result.height)
                viewModel.recognizeText(result.text)
            }
            manager.startCamera(previewView)
            previewView
        })
        // Canvas für Bounding Boxes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val viewWidth = size.width
            val viewHeight = size.height
            val (bitmapWidth, bitmapHeight) = cameraBitmapSize

            boundingBoxes.forEach { rect ->
                val scaledRect = scaleRect(
                    rect,
                    bitmapWidth = bitmapWidth,
                    bitmapHeight = bitmapHeight,
                    viewWidth = viewWidth.toInt(),
                    viewHeight = viewHeight.toInt()
                )

                drawRect(
                    color = Color.Yellow.copy(alpha = 0.3f),
                    topLeft = Offset(scaledRect.left.toFloat(), scaledRect.top.toFloat()),
                    size = Size(scaledRect.width().toFloat(), scaledRect.height().toFloat()),
                    style = Stroke(width = 3f)
                )
            }
        }

        // Text Overlay Card
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

        // Floating Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    recognizedText.let { viewModel.recognizeText(it) }
                    Toast.makeText(context, "OCR erneut ausgelöst", Toast.LENGTH_SHORT).show()
                }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Refresh, contentDescription = "OCR erneut", tint = Color.White)
                    Text("OCR", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }

            FloatingActionButton(
                onClick = {
                    saveTextAsFile(context, recognizedText)
                }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Save, contentDescription = "Text speichern", tint = Color.White)
                    Text("Speichern", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

// Skalierungsfunktion
fun scaleRect(
    rect: Rect,
    bitmapWidth: Int,
    bitmapHeight: Int,
    viewWidth: Int,
    viewHeight: Int
): Rect {
    val scaleX = viewWidth.toFloat() / bitmapWidth.toFloat()
    val scaleY = viewHeight.toFloat() / bitmapHeight.toFloat()

    return Rect(
        (rect.left * scaleX).toInt(),
        (rect.top * scaleY).toInt(),
        (rect.right * scaleX).toInt(),
        (rect.bottom * scaleY).toInt()
    )
}
