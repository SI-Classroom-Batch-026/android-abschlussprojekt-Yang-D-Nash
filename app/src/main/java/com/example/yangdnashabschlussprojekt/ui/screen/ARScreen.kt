package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.R
import com.example.yangdnashabschlussprojekt.ui.component.camera.HoldToScanButton
import com.example.yangdnashabschlussprojekt.ui.component.camera.live.CameraWithLiveObjects
import com.example.yangdnashabschlussprojekt.ui.component.camera.overlay.ARResultOverlay
import com.example.yangdnashabschlussprojekt.ui.component.camera.overlay.ScanningLaserOverlay
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.triggerVibration
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ARScreen(
    arViewModel: ARViewModel = koinViewModel(),
    cameraManager: CameraXManager = koinInject()
) {
    val context = LocalContext.current
    val isCloudLoading by arViewModel.isCloudLoading.collectAsState()
    val isCloudResult by arViewModel.isCloudResult.collectAsState()
    val label by arViewModel.detectedObjectLabel.collectAsState()
    val boxes by arViewModel.boundingBoxes.collectAsState()

    // NEU: Den Translations-Status beobachten
    val translationStatus by arViewModel.translationStatus.collectAsState()

    LaunchedEffect(isCloudResult) {
        if (isCloudResult) triggerVibration(context)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        CameraWithLiveObjects(
            isTextMode = false,
            onAnalyze = { arViewModel.analyze(it) },
            onCameraReady = { preview, owner, _ ->
                cameraManager.startCamera(preview, owner, arViewModel)
            }
        )
        ARResultOverlay(boxes = boxes)

        AnimatedVisibility(
            visible = isCloudLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ScanningLaserOverlay(Color(0xFF00FFCC))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                        radius = 900f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(100.dp))

            // ANGEPASST: Sichtbar wenn Label ODER Translation-Status vorhanden
            AnimatedVisibility(
                visible = (label.isNotEmpty() || translationStatus.isNotEmpty()) && !isCloudResult,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut()
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.3f)),
                    modifier = Modifier.padding(16.dp)
                ) {
                    // ANGEPASST: Text-Logik für dynamische Anzeige
                    Text(
                        text = if (translationStatus.isNotEmpty()) translationStatus.uppercase() else label.uppercase(),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        // Gelb während der Übersetzung, Türkis wenn fertig
                        color = if (translationStatus.isNotEmpty()) Color(0xFFFFEB3B) else Color(0xFF00FFCC),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .animateContentSize()
            ) {
                AnimatedContent(
                    targetState = isCloudResult,
                    label = "ButtonSwitch"
                ) { isResult ->
                    if (isResult) {
                        Button(
                            onClick = {
                                triggerVibration(context)
                                arViewModel.resetCloudResult()
                            },
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth(0.6f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC)),
                            elevation = ButtonDefaults.buttonElevation(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.btn_system_reset),
                                color = Color.Black,
                                fontWeight = FontWeight.Black
                            )
                        }
                    } else if (!isCloudLoading) {
                        HoldToScanButton(onTrigger = {
                            triggerVibration(context)
                            cameraManager.captureForCloudScan(
                                onCaptured = { arViewModel.analyzeWithCloudVision(it) },
                                onError = { }
                            )
                        })
                    }
                }
            }
        }
    }
}