package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.CustomSnackbarHost
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ARResultOverlay
import com.example.yangdnashabschlussprojekt.ui.component.overlay.ScanningLaserOverlay
import com.example.yangdnashabschlussprojekt.ui.component.text.HoldToScanButton
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ARScreen(
    arViewModel: ARViewModel = koinViewModel(),
    textViewModel: TextViewModel = koinViewModel(),
    cameraManager: CameraXManager = koinInject()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    // Vibration-Effekt während des Scannens
    LaunchedEffect(isCloudLoading) {
        if (isCloudLoading) {
            triggerVibration(context)
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(hostState = snackbarHostState, isTextMode = false)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color.Black)) {
            if (hasCameraPermission) {
                CameraWithLiveObjects(
                    cameraManager = cameraManager,
                    arViewModel = arViewModel,
                    textViewModel = textViewModel,
                    isObjectDetectionMode = true,
                    detectedObjectLabel = detectedObjectLabel
                )

                if (isCloudLoading) {
                    ScanningLaserOverlay(laserColor = Color.Cyan)
                }

                // HUD-Overlay für Resultate
                AnimatedVisibility(
                    visible = isCloudResult && !isCloudLoading,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp).zIndex(10f)
                ) {
                    ARResultOverlay(label = detectedObjectLabel, onReset = { arViewModel.resetCloudResult() })
                }

                if (!isCloudResult && !isCloudLoading) {
                    Box(Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)) {
                        HoldToScanButton(onTrigger = {
                            cameraManager.captureForCloudScan(
                                onCaptured = { arViewModel.analyzeWithCloudVision(it) },
                                onError = { Log.e("AR", "Error: $it") }
                            )
                        })
                    }
                }
            }

            if (isCloudLoading) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(0.3f)).zIndex(20f), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.Cyan)
                        Text("ANALYZING...", color = Color.Cyan, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    }
}

/**
 * Universelles Scanning Overlay für AR und Text
 */
