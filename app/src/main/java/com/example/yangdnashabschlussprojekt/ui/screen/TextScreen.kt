package com.example.yangdnashabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.FullScreenScannerOverlay
import com.example.yangdnashabschlussprojekt.ui.component.overlay.TextBoundingBoxOverlay
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

    // States aus dem ViewModel beobachten
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isAnalyzing by textViewModel.isAnalyzing.collectAsState()
    val boundingBoxes by textViewModel.boundingBoxes.collectAsState()
    val isSingleBlock by textViewModel.isSingleBlockMode.collectAsState()
    val isAuthenticated by userRepository.isAuthenticated.collectAsState()

    var showModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        textViewModel.uiEvent.collect { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
    }
    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            triggerVibration(context)
            showModal = true
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            cameraManager.stopAnalysis()
            textViewModel.stopAudio()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = Color.Black
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {

            CameraWithLiveObjects(
                isObjectDetectionMode = false,
                onAnalyze = { imageProxy -> textViewModel.analyze(imageProxy) },
                onCameraReady = { preview, owner, _ ->
                    cameraManager.startCamera(preview, owner, textViewModel)
                }
            )
            if (boundingBoxes.isNotEmpty()) {
                TextBoundingBoxOverlay(
                    boxes = boundingBoxes,
                    onBoxClicked = { box ->
                        triggerVibration(context)
                        textViewModel.onBoxClicked(box)
                    }
                )
            }
            if (isAnalyzing) {
                FullScreenScannerOverlay()
            }
            Box(Modifier.padding(16.dp).align(Alignment.TopStart)) {
                StatusPillUI(isAnalyzing)
            }
            if (cloudState is CloudRecognitionState.Loading) {
                CloudProcessingUI()
            }
            if (recognizedText.isNotBlank() && !isAnalyzing) {
                Button(
                    onClick = { textViewModel.continueAnalysis() },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("RESTART SCAN", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            AnimatedVisibility(
                visible = recognizedText.isNotBlank() && !showModal && cloudState is CloudRecognitionState.Idle,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 180.dp)
                    .zIndex(10f)
            ) {
                BottomTextCard(
                    recognizedText = recognizedText,
                    isSingleBlock = isSingleBlock
                )
            }

            Box(
                Modifier.fillMaxSize().padding(bottom = 110.dp, end = 16.dp).zIndex(20f),
                Alignment.BottomEnd
            ) {
                TextScreenFABs(
                    onLiveToggle = {
                        triggerVibration(context)
                        textViewModel.toggleLiveDetection()
                    },
                    isLiveActive = isAnalyzing,
                    onSaveClick = { textViewModel.onSaveToCloudClicked() },
                    isSaveButtonEnabled = isAuthenticated && recognizedText.isNotBlank(),
                    onHistoryClick = onNavigateToHistory,
                    onCloudScanTriggered = {
                        cameraManager.captureForCloudScan(
                            onCaptured = { base64 -> textViewModel.recognizeTextViaCloud(base64) },
                            onError = { textViewModel.continueAnalysis() }
                        )
                    }
                )
            }
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