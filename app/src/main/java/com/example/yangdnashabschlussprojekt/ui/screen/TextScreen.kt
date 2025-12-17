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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Kameraberechtigung ist notwendig!",
                    withDismissAction = true
                )
            }
        }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    DisposableEffect(lifecycleOwner, vibrator) {
        onDispose {
            cameraManager.unbindAll()
            cameraExecutor.shutdown()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.cancel()
            }
        }
    }
    LaunchedEffect(snackbarHostState) {
        textViewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                withDismissAction = true
            )
        }
    }
    LaunchedEffect(highlight) {
        if (highlight) {
            vibrator?.vibrate(VibrationEffect.createOneShot(120, VibrationEffect.DEFAULT_AMPLITUDE))
            highlight = false
        }
    }
    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Cloud-Analyse und Übersetzung abgeschlossen.",
                    withDismissAction = false
                )
            }
        }
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        snackbarHost = {}
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
            if (isLoading) {
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
                        Text(text = "Analysiere Bild in der Cloud...")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "(Timeout in 3s)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                TextScreenFABs(
                    onRestartClick = {
                        textViewModel.continueAnalysis()
                        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Analyse neu gestartet.",
                                withDismissAction = false
                            )
                        }
                    },
                    isRestartButtonEnabled = !isAnalyzing && recognizedText.isNotBlank(),
                    onSaveClick = {
                        textViewModel.saveCurrentTextToHistory()
                        textViewModel.saveTextToCloud()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Text wurde erfolgreich gespeichert.",
                                withDismissAction = false
                            )
                        }
                    },
                    onHistoryClick = onNavigateToHistory,
                    isSaveButtonEnabled = isAuthenticated && recognizedText.isNotBlank(),
                )
            }
            CustomSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 150.dp)
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
                    onCloudScan = scanCheck@{
                        if (textViewModel.cloudRecognitionState.value is CloudRecognitionState.Loading) return@scanCheck
                        showModal = false
                        val timeoutJob = scope.launch {
                            delay(3000)
                            if (textViewModel.cloudRecognitionState.value is CloudRecognitionState.Loading) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Cloud-Scan-Vorgang hat das Zeitlimit überschritten.",
                                        withDismissAction = true
                                    )
                                }
                                textViewModel.setCloudRecognitionState(CloudRecognitionState.Error("Timeout (Kamera/Cloud)"))
                            }
                        }
                        cameraManager.captureForCloudScan(
                            onCaptured = { base64Image: String ->
                                timeoutJob.cancel()
                                textViewModel.recognizeTextViaCloud(base64Image)
                                highlight = true
                            },
                            onError = { e: Exception ->
                                timeoutJob.cancel()
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Kamerafehler: ${e.message}",
                                        withDismissAction = true
                                    )
                                }
                                textViewModel.setCloudRecognitionState(CloudRecognitionState.Error("Aufnahme fehlgeschlagen: ${e.message}"))
                            }
                        )
                    }
                )
            }
        }
    }
}