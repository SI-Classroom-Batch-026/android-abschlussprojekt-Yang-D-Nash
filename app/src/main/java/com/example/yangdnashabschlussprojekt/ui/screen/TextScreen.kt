package com.example.yangdnashabschlussprojekt.ui.screen

import android.os.Environment
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var cameraManager by remember { mutableStateOf<CameraXManager?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(factory = { ctx ->
                val previewView = PreviewView(ctx)
                val manager = CameraXManager(ctx, cameraExecutor) { text ->
                    viewModel.recognizeText(text)
                }
                cameraManager = manager
                manager.startCamera(previewView)
                previewView
            }, modifier = Modifier.fillMaxSize())
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Erkannter Text:")
            Text(recognizedText)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Übersetzt:")
            Text(translatedText)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "ocr-${System.currentTimeMillis()}.jpg"
                )
                cameraManager?.takePhoto(file, { uri ->
                    // Optional: OCR nach Foto erneut ausführen
                }, { error ->
                    // Fehler behandeln
                })
            }) {
                Text("Foto aufnehmen")
            }
        }
    }
}
