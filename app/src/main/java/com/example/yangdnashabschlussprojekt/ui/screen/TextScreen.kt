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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.CustomSnackbarHost
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

    val isAuthenticated by userRepository.isAuthenticated.collectAsState()
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isAnalyzing by textViewModel.isAnalyzing.collectAsState()

    val isLoading = cloudState is CloudRecognitionState.Loading
    var highlight by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            scope.launch { snackbarHostState.showSnackbar("Kameraberechtigung notwendig!") }
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

    val triggerCloudScan = {
        if (cloudState !is CloudRecognitionState.Loading) {
            val timeoutJob = scope.launch {
                delay(4000) // Etwas mehr Puffer für Cloud
                if (textViewModel.cloudRecognitionState.value is CloudRecognitionState.Loading) {
                    snackbarHostState.showSnackbar("Zeitüberschreitung beim Cloud-Scan.")
                    textViewModel.setCloudRecognitionState(CloudRecognitionState.Error("Timeout"))
                }
            }

            cameraManager.captureForCloudScan(
                onCaptured = { base64 ->
                    timeoutJob.cancel()
                    textViewModel.recognizeTextViaCloud(base64)
                    highlight = true
                },
                onError = { e ->
                    timeoutJob.cancel()
                    textViewModel.setCloudRecognitionState(CloudRecognitionState.Error(e.message ?: "Fehler"))
                }
            )
        }
    }

    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            snackbarHostState.showSnackbar("Cloud-Analyse abgeschlossen.")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        snackbarHost = { }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            CameraWithLiveObjects(
                cameraManager = cameraManager,
                textViewModel = textViewModel,
                arViewModel = arViewModel,
                isObjectDetectionMode = false,
                detectedObjectLabel = detectedObjectLabel,
            )

            BottomTextCard(
                recognizedText = recognizedText,
                translatedText = translatedText,
                cloudRecognitionState = cloudState,
                onEditClick = { showModal = true },
                modifier = Modifier.align(Alignment.BottomStart)
            )

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
                            .padding(24.dp)
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Analysiere Bild...")
                    }
                }
            }

            Box(modifier = Modifier.align(Alignment.BottomEnd)) {
                TextScreenFABs(
                    onRestartClick = {
                        textViewModel.continueAnalysis()
                        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                    },
                    isRestartButtonEnabled = !isAnalyzing && recognizedText.isNotBlank(),
                    onSaveClick = {
                        textViewModel.saveCurrentTextToHistory()
                        textViewModel.saveTextToCloud()
                    },
                    isSaveButtonEnabled = isAuthenticated && recognizedText.isNotBlank(),
                    onHistoryClick = onNavigateToHistory,
                    onCloudScanTriggered = { triggerCloudScan() } // Hier geht's ab!
                )
            }

            CustomSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 160.dp) // Damit sie über den FABs schwebt
                    .zIndex(1f)
            )

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
                        triggerCloudScan()
                    }
                )
            }
        }
    }
}