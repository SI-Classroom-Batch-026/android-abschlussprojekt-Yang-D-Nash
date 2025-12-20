package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.text.HoldToScanButton
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.shared.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

@Composable
fun ARScreen(
    arViewModel: ARViewModel = koinViewModel(),
    textViewModel: TextViewModel = koinViewModel(),
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Kamera-Ressourcen
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }

    // States
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    // --- NEU: Permission Logic ---
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Lifecycle Management für den Executor
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            // Kamera nur anzeigen, wenn Permission da ist
            CameraWithLiveObjects(
                cameraManager = cameraManager,
                arViewModel = arViewModel,
                textViewModel = textViewModel,
                isObjectDetectionMode = true,
                detectedObjectLabel = detectedObjectLabel
            )

            // Result Card (Oben)
            AnimatedVisibility(
                visible = isCloudResult && !isCloudLoading,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp)
            ) {
                Card(
                    onClick = { arViewModel.resetCloudResult() },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C).copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(0.85f).padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Cyan,
                            modifier = Modifier.size(30.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("IDENTIFIZIERT", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            Text(detectedObjectLabel, color = Color.White, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            // Scan Button (Unten)
            HoldToScanButton(
                onTrigger = {
                    Log.d("AR_DEBUG", "Button getriggert")
                    cameraManager.captureForCloudScan(
                        onCaptured = { base64 ->
                            Log.d("AR_DEBUG", "Bild erfolgreich gecaptured")
                            arViewModel.analyzeWithCloudVision(base64)
                        },
                        onError = { Log.e("AR_DEBUG", "Capture-Fehler: ${it.message}") }
                    )
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)
            )
        } else {
            // Fallback, wenn keine Permission gegeben wurde
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Kamera-Berechtigung wird benötigt", color = Color.White)
            }
        }

        // Loading Overlay
        if (isCloudLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.Cyan)
            }
        }
    }
}