package com.example.yangdnashabschlussprojekt.ui.screen

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yangdnashabschlussprojekt.R
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.camera.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.camera.overlay.FullScreenScannerOverlay
import com.example.yangdnashabschlussprojekt.ui.component.camera.overlay.TextBoundingBoxOverlay
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
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
    val isSingleBlock by textViewModel.isSingleBlockMode.collectAsState()
    val isAuthenticated by userRepository.isAuthenticated.collectAsState()

    // NEU: Beobachtet den Übersetzungsstatus (z.B. "KI übersetzt...")
    val translationStatus by textViewModel.translationStatus.collectAsState()

    var showModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        textViewModel.uiEvent.collect { message ->
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
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CameraWithLiveObjects(
                isTextMode = isAnalyzing,
                onAnalyze = { textViewModel.analyze(it) },
                onCameraReady = { preview, owner, _ ->
                    cameraManager.startCamera(preview, owner, textViewModel)
                }
            )

            // --- NEU: DYNAMISCHER ÜBERSETZUNGS-STATUS OVERLAY ---
            AnimatedVisibility(
                visible = translationStatus.isNotEmpty(),
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .zIndex(100f)
            ) {
                Surface(
                    color = Color(0xFFFFEB3B).copy(alpha = 0.9f), // Gelb für Aufmerksamkeit
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = translationStatus.uppercase(),
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isAnalyzing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
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

            // RESET BUTTON
            AnimatedVisibility(
                visible = (recognizedText.isNotBlank() || boundingBoxes.isNotEmpty()) && !isAnalyzing,
                enter = slideInVertically(),
                exit = slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 64.dp)
                    .zIndex(50f)
            ) {
                Button(
                    onClick = {
                        triggerVibration(context)
                        textViewModel.continueAnalysis()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White
                    ),
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Rounded.Close,
                        contentDescription = stringResource(R.string.content_desc_clear),
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF00E5FF)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.btn_scan_reset),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF)
                    )
                }
            }

            // BOTTOM CARD (Ergebnis-Vorschau)
            AnimatedVisibility(
                visible = recognizedText.isNotBlank() && !showModal,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 140.dp)
                    .zIndex(10f)
            ) {
                BottomTextCard(
                    recognizedText = recognizedText,
                    isSingleBlock = isSingleBlock
                )
            }

            // FABs (Steuerung)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 110.dp, end = 16.dp)
                    .zIndex(20f),
                contentAlignment = Alignment.BottomEnd
            ) {
                TextScreenFABs(
                    onLiveToggle = {
                        triggerVibration(context)
                        textViewModel.triggerLocalScan()
                    },
                    isLiveActive = isAnalyzing,
                    onSaveClick = { textViewModel.onSaveToCloudClicked() },
                    isSaveButtonEnabled = isAuthenticated && recognizedText.isNotBlank(),
                    onHistoryClick = onNavigateToHistory,
                    isCloudScanEnabled = cloudState !is CloudRecognitionState.Loading,
                    onCloudScanTriggered = {
                        triggerVibration(context)
                        cameraManager.captureForCloudScan(
                            onCaptured = { textViewModel.recognizeTextViaCloud(it) },
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