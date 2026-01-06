package com.example.yangdnashabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.FullScreenScannerOverlay
import com.example.yangdnashabschlussprojekt.ui.component.overlay.TextBoundingBoxOverlay
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.CloudProcessingUI
import com.example.yangdnashabschlussprojekt.ui.component.text.RecognitionModalSheet
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
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
    val isSingleBlock by textViewModel.isSingleBlockMode.collectAsState()
    val isAuthenticated by userRepository.isAuthenticated.collectAsState()

    var showModal by remember { mutableStateOf(false) }
    val uiEvent = textViewModel.uiEvent

    LaunchedEffect(Unit) {
        uiEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            triggerVibration(context)
            showModal = true
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
            if (isAnalyzing) {
                FullScreenScannerOverlay()
            }
            if (boundingBoxes.isNotEmpty()) {
                TextBoundingBoxOverlay(
                    boxes = boundingBoxes,
                    onBoxClicked = { box ->
                        triggerVibration(context)
                        textViewModel.onBoxClicked(box)
                    }
                )
            }
            if (cloudState is CloudRecognitionState.Loading) {
                CloudProcessingUI()
            }
            if ((recognizedText.isNotBlank() || boundingBoxes.isNotEmpty()) && !isAnalyzing) {
                Button(
                    onClick = { textViewModel.continueAnalysis() },
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF).copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("CLEAR", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            AnimatedVisibility(
                visible = recognizedText.isNotBlank() && !showModal,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 180.dp).zIndex(10f)
            ) {
                BottomTextCard(recognizedText = recognizedText, isSingleBlock = isSingleBlock)
            }
            Box(Modifier.fillMaxSize().padding(bottom = 110.dp, end = 16.dp).zIndex(20f), Alignment.BottomEnd) {
                TextScreenFABs(
                    onLiveToggle = {
                        triggerVibration(context)
                        textViewModel.triggerLocalScan()
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
                    onDismiss = { showModal = false },
                    onTextEdited = { textViewModel.recognizeText(it) },
                    onSaveToCloud = { textViewModel.onSaveToCloudClicked() },
                    isLoggedIn = isAuthenticated
                )
            }
        }
    }
}