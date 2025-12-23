package com.example.yangdnashabschlussprojekt.ui.screen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ScanningLaserOverlay
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.RecognitionModalSheet
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CloudRecognitionState
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun TextScreen(
    textViewModel: TextViewModel = koinViewModel(),
    arViewModel: ARViewModel = koinViewModel(),
    userRepository: UserRepository = koinInject(),
    cameraManager: CameraXManager = koinInject(),
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isAnalyzing by textViewModel.isAnalyzing.collectAsState()
    val isAuthenticated by userRepository.isAuthenticated.collectAsState()
    var showModal by remember { mutableStateOf(false) }
    LaunchedEffect(isAnalyzing) {
        if (isAnalyzing) {
            cameraManager.setAnalyzer { imageProxy ->
                textViewModel.analyzeImageProxy(imageProxy)
            }
        } else {
            cameraManager.setAnalyzer { it.close() }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            cameraManager.setAnalyzer { it.close() }
        }
    }
    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            triggerVibration(context)
            showModal = true
        }
    }
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize().imePadding()
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            CameraWithLiveObjects(
                cameraManager = cameraManager,
                arViewModel = arViewModel,
                textViewModel = textViewModel,
                isObjectDetectionMode = false,
                detectedObjectLabel = ""
            )
            if (cloudState is CloudRecognitionState.Loading) {
                ScanningLaserOverlay(laserColor = Color(0xFFFF00FF))
                Box(
                    Modifier.fillMaxSize().background(Color.Black.copy(0.5f)).zIndex(30f),
                    Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFFFF00FF), strokeWidth = 4.dp)
                        Spacer(Modifier.height(16.dp))
                        Text("DECRYPTING...", color = Color(0xFFFF00FF), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            AnimatedVisibility(
                visible = recognizedText.isNotBlank() && !showModal,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 120.dp, start = 16.dp)
                    .zIndex(10f)
            ) {
                BottomTextCard(recognizedText)
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp)
                    .zIndex(20f),
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
                            onError = { Log.e("TEXT_SCREEN", "Capture Error: $it") }
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