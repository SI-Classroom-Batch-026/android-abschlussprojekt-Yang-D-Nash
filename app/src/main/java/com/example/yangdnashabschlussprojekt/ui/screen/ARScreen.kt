package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight // Hinzugefügt für FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ARResultOverlay
import com.example.yangdnashabschlussprojekt.ui.component.text.HoldToScanButton
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ARScreen(
    arViewModel: ARViewModel = koinViewModel(),
    textViewModel: TextViewModel = koinViewModel(), // Hinzugefügt für CameraWithLiveObjects
    cameraManager: CameraXManager = koinInject()
) {
    val context = LocalContext.current
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasCameraPermission = it }

    LaunchedEffect(Unit) {
        cameraManager.isTextMode = false
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            CameraWithLiveObjects(
                cameraManager = cameraManager,
                arViewModel = arViewModel,
                textViewModel = textViewModel, // Jetzt übergeben
                isObjectDetectionMode = true,
                detectedObjectLabel = detectedObjectLabel
            )

            AnimatedVisibility(
                visible = isCloudResult && !isCloudLoading,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it },
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp).zIndex(10f)
            ) {
                ARResultOverlay(
                    label = detectedObjectLabel,
                    onReset = { arViewModel.resetCloudResult() }
                )
            }

            if (!isCloudResult && !isCloudLoading) {
                Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)) {
                    HoldToScanButton(onTrigger = {
                        cameraManager.captureForCloudScan(
                            onCaptured = { arViewModel.analyzeWithCloudVision(it) },
                            onError = { Log.e("AR_SCREEN", "Capture failed: $it") }
                        )
                    })
                }
            }
        }

        if (isCloudLoading) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(0.7f)).zIndex(20f), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.Cyan, strokeWidth = 4.dp)
                    Spacer(Modifier.height(16.dp))
                    Text("UPLOADING TO CLOUD...", color = Color.Cyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}