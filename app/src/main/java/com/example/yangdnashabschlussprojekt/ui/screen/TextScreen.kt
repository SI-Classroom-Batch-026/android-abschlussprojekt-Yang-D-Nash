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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private fun triggerVibration(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
}
@Composable
fun TextScreen(
    textViewModel: TextViewModel = koinViewModel(),
    arViewModel: ARViewModel = koinViewModel(),
    userRepository: UserRepository = koinInject(),
    cameraManager: CameraXManager = koinInject(),
    onNavigateToHistory: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isAuthenticated by userRepository.isAuthenticated.collectAsState()
    val recognizedText by textViewModel.recognizedText.collectAsState()
    val translatedText by textViewModel.translatedText.collectAsState()
    val cloudState by textViewModel.cloudRecognitionState.collectAsState()
    val isAnalyzing by textViewModel.isAnalyzing.collectAsState()
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()
    var showModal by remember { mutableStateOf(false) }
    LaunchedEffect(recognizedText, isAnalyzing) {
        if (recognizedText.isNotEmpty() && !isAnalyzing) {
            showModal = true
        }
    }
    LaunchedEffect(Unit) {
        cameraManager.isTextMode = true
    }
    LaunchedEffect(Unit) {
        textViewModel.uiEvent.collectLatest { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(cloudState) {
        if (cloudState is CloudRecognitionState.Success) {
            triggerVibration(context)
            showModal = true
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) scope.launch { snackbarHostState.showSnackbar("Kamera benötigt.") }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        snackbarHost = {
            CustomSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 100.dp).zIndex(50f),
                isTextMode = true
            )
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            CameraWithLiveObjects(cameraManager, arViewModel, textViewModel, false, detectedObjectLabel)
            androidx.compose.animation.AnimatedVisibility(
                visible = recognizedText.isNotBlank() && !showModal && cloudState !is CloudRecognitionState.Loading,
                modifier = Modifier.align(Alignment.BottomStart).zIndex(10f)
            ) {
                BottomTextCard(recognizedText)
            }
            Box(Modifier.fillMaxSize().padding(16.dp).zIndex(20f), Alignment.BottomEnd) {
                TextScreenFABs(
                    onRestartClick = { textViewModel.continueAnalysis(); triggerVibration(context) },
                    isRestartButtonEnabled = !isAnalyzing,
                    onSaveClick = { textViewModel.saveCurrentTextToHistory() },
                    isSaveButtonEnabled = isAuthenticated && translatedText.isNotBlank(),
                    onHistoryClick = onNavigateToHistory,
                    onCloudScanTriggered = {
                        cameraManager.captureForCloudScan(
                            onCaptured = { textViewModel.recognizeTextViaCloud(it) },
                            onError = { textViewModel.setCloudRecognitionState(CloudRecognitionState.Error(it.message ?: "Error")) }
                        )
                    }
                )
            }
            if (cloudState is CloudRecognitionState.Loading) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(0.6f)).zIndex(30f), Alignment.Center) {
                    CircularProgressIndicator(color = Color.Magenta)
                }
            }
            if (showModal) {
                RecognitionModalSheet(
                    recognizedText, translatedText,
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