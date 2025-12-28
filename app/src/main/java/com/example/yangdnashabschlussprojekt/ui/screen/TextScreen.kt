package com.example.yangdnashabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.text.*
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CloudRecognitionState
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun TextScreen(
    textViewModel: TextViewModel = koinViewModel(),
    userRepository: UserRepository = koinInject(),
    cameraManager: CameraXManager = koinInject(),
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isAnalyzing by textViewModel.isAnalyzing.collectAsState()
    val boundingBoxes by textViewModel.boundingBoxes.collectAsState()
    val isAuthenticated by userRepository.isAuthenticated.collectAsState()
    var showModal by remember { mutableStateOf(false) }

    // 1. UI Events (Toasts)
    LaunchedEffect(Unit) {
        textViewModel.uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // 2. Cloud Success -> Modal öffnen
    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            triggerVibration(context)
            showModal = true
        }
    }

    // 3. Analyse stoppen wenn pausiert
    LaunchedEffect(isAnalyzing) {
        if (!isAnalyzing) {
            cameraManager.stopAnalysis()
        }
    }

    // 4. Cleanup beim Verlassen des Screens
    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopAnalysis()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.Black
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {

            // Die Kamera nutzt jetzt direkt das textViewModel als Analyzer Interface
            CameraWithLiveObjects(
                isObjectDetectionMode = false,
                onAnalyze = { /* Unbenutzt, da ViewModel direkt injiziert wird */ },
                onCameraReady = { preview, owner, _ ->
                    cameraManager.startCamera(preview, owner, textViewModel)
                }
            )

            // Overlays
            if (isAnalyzing && !showModal) {
                AnimatedBoundingBoxes(boundingBoxes)
                CyberFocusFrame()
            }

            StatusPillUI(isAnalyzing)

            if (cloudState is CloudRecognitionState.Loading) {
                CloudProcessingUI()
            }

            // Erkannter Text Karte
            AnimatedVisibility(
                visible = recognizedText.isNotBlank() && !showModal && cloudState is CloudRecognitionState.Idle,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 190.dp, start = 20.dp)
                    .zIndex(10f)
            ) {
                BottomTextCard(recognizedText)
            }

            // FABs
            Box(
                Modifier.fillMaxSize().padding(bottom = 110.dp, end = 16.dp).zIndex(20f),
                Alignment.BottomEnd
            ) {
                TextScreenFABs(
                    onLiveToggle = {
                        textViewModel.toggleLiveDetection()
                        triggerVibration(context)
                    },
                    isLiveActive = isAnalyzing,
                    onSaveClick = { textViewModel.saveCurrentTextToHistory() },
                    isSaveButtonEnabled = isAuthenticated && translatedText.isNotBlank(),
                    onHistoryClick = onNavigateToHistory,
                    onCloudScanTriggered = {
                        cameraManager.captureForCloudScan(
                            onCaptured = { base64 ->
                                textViewModel.pauseAnalysis()
                                textViewModel.recognizeTextViaCloud(base64)
                            },
                            onError = { _ -> textViewModel.continueAnalysis() }
                        )
                    }
                )
            }

            // Resultat Modal
            if (showModal) {
                RecognitionModalSheet(
                    recognizedText = recognizedText,
                    translatedText = translatedText,
                    onDismiss = {
                        showModal = false
                        textViewModel.continueAnalysis()
                    },
                    onTextEdited = { textViewModel.recognizeText(it) },
                    onSaveToCloud = { textViewModel.onSaveToCloudClicked() },
                    isLoggedIn = isAuthenticated
                )
            }
        }
    }
}

@Composable
fun AnimatedBoundingBoxes(boxes: List<com.example.yangdnashabschlussprojekt.data.local.database.model.box.TimedBoundingBox>) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        boxes.forEach { box ->
            // Skalierung der Boxen auf die aktuelle Display-Größe
            val scaleX = size.width / box.frameWidth
            val scaleY = size.height / box.frameHeight

            drawRect(
                color = Color.Cyan.copy(alpha = alpha),
                topLeft = Offset(box.left * scaleX, box.top * scaleY),
                size = Size((box.right - box.left) * scaleX, (box.bottom - box.top) * scaleY),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}