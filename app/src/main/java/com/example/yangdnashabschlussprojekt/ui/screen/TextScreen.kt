package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.CustomSnackbarHost
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.text.BottomTextCard
import com.example.yangdnashabschlussprojekt.ui.component.text.RecognitionModalSheet
import com.example.yangdnashabschlussprojekt.ui.component.text.TextScreenFABs
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CloudRecognitionState
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.util.concurrent.Executors

private fun createVibrator(context: Context): Vibrator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(VibratorManager::class.java)
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
}

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
    val snackbarHostState = remember { SnackbarHostState() }
    val vibrator = remember { createVibrator(context) }

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }

    // States abrufen
    val isAuthenticated by userRepository.isAuthenticated.collectAsState()
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isAnalyzing by textViewModel.isAnalyzing.collectAsState()
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()

    val isLoading = cloudState is CloudRecognitionState.Loading
    var showModal by remember { mutableStateOf(false) }

    // Permission Check
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            scope.launch { snackbarHostState.showSnackbar("Kamera wird benötigt!") }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            cameraManager.unbindAll()
            cameraExecutor.shutdown()
        }
    }

    // Automatisches Öffnen des Modals
    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            showModal = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        snackbarHost = { }
    ) { paddingValues ->
        // WICHTIG: Die Box nutzt fillMaxSize, um den gesamten Viewport zu füllen
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // 1. Kamera (Hintergrund)
            CameraWithLiveObjects(
                cameraManager = cameraManager,
                textViewModel = textViewModel,
                arViewModel = arViewModel,
                isObjectDetectionMode = false,
                detectedObjectLabel = detectedObjectLabel,
            )

            // 2. UI-Layer (Vorschau Card)
            // Wir nutzen hier eine explizite AnimatedVisibility mit hohem zIndex
            Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
                AnimatedVisibility(
                    visible = recognizedText.isNotBlank() && !showModal && !isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.BottomStart)
                ) {
                    // Hier die Card Komponente
                    BottomTextCard(
                        recognizedText = recognizedText
                    )
                }
            }

            // 3. FAB Layer (Ganz oben rechts/unten)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .zIndex(20f),
                contentAlignment = Alignment.BottomEnd
            ) {
                TextScreenFABs(
                    onRestartClick = {
                        textViewModel.continueAnalysis()
                        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    },
                    isRestartButtonEnabled = !isAnalyzing,
                    onSaveClick = {
                        textViewModel.saveCurrentTextToHistory()
                        scope.launch { snackbarHostState.showSnackbar("Gespeichert!") }
                    },
                    isSaveButtonEnabled = isAuthenticated && translatedText.isNotBlank(),
                    onHistoryClick = onNavigateToHistory,
                    onCloudScanTriggered = {
                        if (cloudState !is CloudRecognitionState.Loading) {
                            cameraManager.captureForCloudScan(
                                onCaptured = { base64 -> textViewModel.recognizeTextViaCloud(base64) },
                                onError = { e -> textViewModel.setCloudRecognitionState(CloudRecognitionState.Error(e.message ?: "Fehler")) }
                            )
                        }
                    }
                )
            }

            // 4. Lade-Overlay
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .zIndex(30f)
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            // 5. Snackbar & Modal
            CustomSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp).zIndex(40f)
            )

            if (showModal) {
                RecognitionModalSheet(
                    recognizedText = recognizedText,
                    translatedText = translatedText,
                    onDismiss = { showModal = false },
                    onTextEdited = { newText -> textViewModel.recognizeText(newText) }
                )
            }
        }
    }
}