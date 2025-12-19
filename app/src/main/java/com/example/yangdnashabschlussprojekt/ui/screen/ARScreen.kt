package com.example.yangdnashabschlussprojekt.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraManager = remember { CameraXManager(context, cameraExecutor) }

    val detectedObjectLabel by arViewModel.detectedObjectLabel.collectAsState()
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        CameraWithLiveObjects(
            cameraManager = cameraManager,
            arViewModel = arViewModel,
            textViewModel = textViewModel,
            isObjectDetectionMode = true,
            detectedObjectLabel = detectedObjectLabel
        )

        // DIE RESULT CARD OBEN
        androidx.compose.animation.AnimatedVisibility(
            visible = isCloudResult && !isCloudLoading,
            enter = androidx.compose.animation.slideInVertically() + androidx.compose.animation.fadeIn(),
            exit = androidx.compose.animation.slideOutVertically() + androidx.compose.animation.fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp)
        ) {
            androidx.compose.material3.Card(
                onClick = { arViewModel.resetCloudResult() }, // Klick zum Zurücksetzen
                colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C).copy(0.9f)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(0.85f).padding(8.dp)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Cyan,
                        modifier = Modifier.size(30.dp)
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(16.dp))
                    androidx.compose.foundation.layout.Column {
                        Text("IDENTIFIZIERT", color = Color.Gray, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                        Text(detectedObjectLabel, color = Color.White, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }

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

        if (isCloudLoading) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.4f)), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator(color = Color.Cyan)
            }
        }
    }
}