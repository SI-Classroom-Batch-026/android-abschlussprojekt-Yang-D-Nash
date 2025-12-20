package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.example.yangdnashabschlussprojekt.ui.component.live.CameraWithLiveObjects
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
    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        hasCameraPermission = it
    }

    LaunchedEffect(Unit) {
        cameraManager.isTextMode = false // Wichtig für den Analyzer Switch
        if (!hasCameraPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasCameraPermission) {
            CameraWithLiveObjects(
                cameraManager = cameraManager,
                arViewModel = arViewModel,
                textViewModel = textViewModel,
                isObjectDetectionMode = true,
                detectedObjectLabel = detectedObjectLabel
            )

            AnimatedVisibility(
                visible = isCloudResult && !isCloudLoading,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 50.dp).zIndex(10f)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Cyan),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Color.Cyan)
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text("SCAN RESULT", color = Color.Cyan.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
                            Text(detectedObjectLabel.uppercase(), color = Color.White, style = MaterialTheme.typography.titleLarge)
                        }
                        IconButton(onClick = { arViewModel.resetCloudResult() }) {
                            Icon(Icons.Default.Close, null, tint = Color.White)
                        }
                    }
                }
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
                    CircularProgressIndicator(color = Color.Cyan)
                    Spacer(Modifier.height(16.dp))
                    Text("PROCESSING AR DATA...", color = Color.Cyan)
                }
            }
        }
    }
}