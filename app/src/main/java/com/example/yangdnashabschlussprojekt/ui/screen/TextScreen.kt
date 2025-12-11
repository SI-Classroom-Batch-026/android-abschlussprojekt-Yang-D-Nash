package com.example.yangdnashabschlussprojekt.ui.screen

import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.camera.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.RecognitionModalSheet
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CloudRecognitionState
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.concurrent.Executors
@Composable
fun TextScreen(
    textViewModel: TextViewModel = koinViewModel(),
    arViewModel: ARViewModel = koinViewModel(),
    userRepository: UserRepository = koinInject(),
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }
    val vibrator = context.getSystemService(Vibrator::class.java)

    val isAuthenticated by userRepository.isAuthenticated.collectAsState()
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isAnalyzing by textViewModel.isAnalyzing.collectAsState()

    val isLoading = cloudState is CloudRecognitionState.Loading

    var highlight by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }

    var showSaveFeedback by remember { mutableStateOf(false) }

    // ❌ KORRIGIERT: Dieses DisposableEffect war redundant und kann entfernt werden,
    // da cameraExecutor in onDispose des Haupteffekts unten heruntergefahren werden kann.
    /*
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
    */

    // 🆕 NEU: Haupteffekt zur Verwaltung der Kamera-Ressourcen im Lifecycle des Composables
    DisposableEffect(lifecycleOwner) {
        // Beim Start des Composables (oder wenn der LifecycleOwner sich ändert)
        // ... hier könnte man cameraManager.startCamera aufrufen,
        //     aber das passiert bereits in CameraWithLiveObjects.

        onDispose {
            // Beim Verlassen des Composables (oder wenn der LifecycleOwner sich ändert)
            // 🚨 KRITISCH: Unbinde alle Usecases, um BufferQueue-Abandonment zu verhindern
            cameraManager.unbindAll()
            cameraExecutor.shutdown() // Füge das Herunterfahren des Executors hier hinzu
        }
    }

    LaunchedEffect(highlight) {
        if (highlight) {
            vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            highlight = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraWithLiveObjects(
            cameraManager = cameraManager,
            textViewModel = textViewModel,
            arViewModel = arViewModel
        )

        BottomTextCard(
            recognizedText = recognizedText,
            translatedText = translatedText,
            cloudRecognitionState = cloudState,
            onEditClick = { showModal = true },
            modifier = Modifier.align(Alignment.BottomStart)
        )

        if (isLoading || (isAnalyzing && recognizedText.isBlank())) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            TextScreenFABs(
                onRestartClick = textViewModel::continueAnalysis,
                isRestartButtonEnabled = !isAnalyzing && recognizedText.isNotBlank(),

                onSaveClick = {
                    if (isAuthenticated) {
                        if (recognizedText.isBlank()) {
                            Toast.makeText(context, "Kein Text zum Speichern vorhanden.", Toast.LENGTH_SHORT).show()
                            return@TextScreenFABs
                        }

                        scope.launch {
                            userRepository.saveTextEntry(recognizedText, translatedText)
                                .onSuccess {
                                    Toast.makeText(context, "Text erfolgreich in Firebase gespeichert!", Toast.LENGTH_SHORT).show()
                                    showSaveFeedback = true
                                    delay(1500)
                                    showSaveFeedback = false
                                }.onFailure { e ->
                                    Toast.makeText(context, "Speichern fehlgeschlagen: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
                    }
                },
                onHistoryClick = onNavigateToHistory,
                isSaveButtonEnabled = isAuthenticated && recognizedText.isNotBlank(),
            )

            AnimatedVisibility(
                visible = showSaveFeedback,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp, end = 16.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Gespeichert",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        if (showModal) {
            RecognitionModalSheet(
                recognizedText = recognizedText,
                onDismiss = { showModal = false },

                onTextEdited = { newText ->
                    textViewModel.recognizeText(newText)
                    showModal = false
                },

                onCloudScan = {
                    showModal = false
                    if (!isLoading) {
                        cameraManager.captureForCloudScan(
                            onCaptured = { base64Image: String ->
                                textViewModel.recognizeTextViaCloud(base64Image)
                                highlight = true
                            },
                            onError = { e: Exception ->
                                Toast.makeText(context, "Kamerafehler: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            )
        }
    }
}