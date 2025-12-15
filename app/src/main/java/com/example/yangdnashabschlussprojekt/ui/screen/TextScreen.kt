package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding // WICHTIG: Für Tastatur-Handling
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
import androidx.core.content.ContextCompat
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
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.CustomSnackbarHost
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

    val snackbarHostState = remember { SnackbarHostState() }

    val vibrator = remember {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val manager = context.getSystemService(VibratorManager::class.java)
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

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

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Kameraberechtigung ist notwendig!", Toast.LENGTH_LONG).show()
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

    LaunchedEffect(snackbarHostState) {
        Log.d("TextScreen", "Starte das Lauschen auf UI Events...")
        textViewModel.uiEvent.collect { message ->
            Log.d("TextScreen", "UI Event empfangen: $message")
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
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(hostState = snackbarHostState)
        },
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

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
                        Text(
                            text = "Analysiere Bild in der Cloud...",
                            style = MaterialTheme.typography.bodyMedium
                        )
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
                    },
                    isRestartButtonEnabled = !isAnalyzing && recognizedText.isNotBlank(),

                    onSaveClick = {
                        textViewModel.saveTextToCloud()
                    },
                    onHistoryClick = onNavigateToHistory,
                    isSaveButtonEnabled = isAuthenticated && recognizedText.isNotBlank(),
                )
            }

            if (showModal) {
                RecognitionModalSheet(
                    recognizedText = recognizedText,
                    onDismiss = { showModal = false },

                    onTextEdited = { newText ->
                        textViewModel.recognizeText(newText)
                        showModal = false // <-- Modal schließen
                    },

                    onCloudScan = scanCheck@{
                        if (textViewModel.cloudRecognitionState.value is CloudRecognitionState.Loading) return@scanCheck

                        showModal = false

                        val timeoutJob = scope.launch {
                            delay(3000)
                            if (textViewModel.cloudRecognitionState.value is CloudRecognitionState.Loading) {
                                Toast.makeText(context, "Cloud-Scan-Vorgang hat das Zeitlimit überschritten.", Toast.LENGTH_LONG).show()
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
                                Toast.makeText(context, "Kamerafehler: ${e.message}", Toast.LENGTH_SHORT).show()
                                textViewModel.setCloudRecognitionState(CloudRecognitionState.Error("Aufnahme fehlgeschlagen: ${e.message}"))
                            }
                        )
                    }
                )
            }
        }
    }
}