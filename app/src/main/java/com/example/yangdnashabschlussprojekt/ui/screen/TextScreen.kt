package com.example.yangdnashabschlussprojekt.ui.screen

import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraWithBoundingBoxes
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CloudRecognitionState
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.concurrent.Executors

@Composable
fun TextScreen(
    textViewModel: TextViewModel = koinViewModel(),
    arViewModel: ARViewModel = koinViewModel(),
    userRepository: UserRepository = koinInject()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }
    val vibrator = context.getSystemService(Vibrator::class.java)

    val isAuthenticated = userRepository.isAuthenticated.collectAsState().value
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isLoading = cloudState is CloudRecognitionState.Loading

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
            textViewModel = textViewModel,
            arViewModel = arViewModel
        )

        BottomTextCard(
            recognizedText = recognizedText,
            translatedText = translatedText,
            cloudRecognitionState = cloudState,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        TextScreenFABs(
            onScanClick = {
                if (!isLoading) {
                    // KORREKTUR: Explizite Typisierung der Lambda-Parameter
                    cameraManager.captureFrameAsBase64(
                        onCaptured = { base64Image: String -> // HIER: Typ String hinzugefügt
                            textViewModel.recognizeTextViaCloud(base64Image)
                            highlight = true
                        },
                        onError = { e: Exception -> // HIER: Typ Exception hinzugefügt
                            Toast.makeText(context, "Kamerafehler: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            },
            onSaveClick = {
                if (isAuthenticated) {
                    if (recognizedText.isBlank()) {
                        Toast.makeText(context, "Kein Text zum Speichern vorhanden.", Toast.LENGTH_SHORT).show()
                        return@TextScreenFABs
                    }

                    scope.launch {
                        val result = userRepository.saveTextEntry(
                            recognizedText = recognizedText,
                            translatedText = translatedText
                        )

                        result.onSuccess {
                            Toast.makeText(context, "Text erfolgreich gespeichert!", Toast.LENGTH_SHORT).show()
                        }.onFailure { e ->
                            Toast.makeText(context, "Speichern fehlgeschlagen: ${e.message}", Toast.LENGTH_LONG).show()
                            println("Firebase Save Error: ${e.message}")
                        }
                    }
                }
            },
            isSaveButtonEnabled = isAuthenticated && recognizedText.isNotBlank(),
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}