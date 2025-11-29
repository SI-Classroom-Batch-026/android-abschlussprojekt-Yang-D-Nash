package com.example.yangdnashabschlussprojekt.ui.screen

import android.content.Context
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import java.io.File
import java.util.concurrent.Executors

@Composable
fun TextScreen(viewModel: TextViewModel) {
    val context = LocalContext.current
    val recognizedText by viewModel.recognizedText.collectAsState()
    val translatedText by viewModel.translatedText.collectAsState()
    var boundingBoxes by remember { mutableStateOf(listOf<android.graphics.Rect>()) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {

        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx)
            val manager = CameraXManager(ctx, cameraExecutor) { text, boxes ->
                boundingBoxes = boxes
                viewModel.recognizeText(text)
            }
            manager.startCamera(previewView)
            previewView
        }, modifier = Modifier.fillMaxSize())

        Canvas(modifier = Modifier.fillMaxSize()) {
            boundingBoxes.forEach { rect ->
                drawRect(
                    color = Color.Yellow.copy(alpha = 0.3f),
                    topLeft = androidx.compose.ui.geometry.Offset(rect.left.toFloat(), rect.top.toFloat()),
                    size = androidx.compose.ui.geometry.Size(rect.width().toFloat(), rect.height().toFloat()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
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

fun saveTextAsFile(context: Context, text: String) {
    try {
        if(text.isBlank()) {
            Toast.makeText(context, "Kein Text zum Speichern", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "ocr-${System.currentTimeMillis()}.txt"
        val file = File(context.getExternalFilesDir(null), fileName)
        file.writeText(text)
        Toast.makeText(context, "Text gespeichert: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Fehler beim Speichern: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}
