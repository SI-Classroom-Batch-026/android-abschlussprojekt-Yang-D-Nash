package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedArViewModel
import com.example.yangdnashabschlussprojekt.shared.SmartVisionAccentCard
import com.example.yangdnashabschlussprojekt.shared.SmartVisionGlassCard
import com.example.yangdnashabschlussprojekt.shared.SmartVisionHeader
import com.example.yangdnashabschlussprojekt.shared.SmartVisionLiveScreenBackground
import com.example.yangdnashabschlussprojekt.shared.SmartVisionScreenBackground
import com.example.yangdnashabschlussprojekt.shared.SmartVisionStatusCard
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SharedArRoute(
    onOpenTextMode: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SharedArViewModel = koinViewModel()
) {
    val selectedImageName by viewModel.selectedImageName.collectAsState()
    val selectedImagePath by viewModel.selectedImagePath.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val primaryLabel by viewModel.primaryLabel.collectAsState()
    val detectedCandidates by viewModel.detectedCandidates.collectAsState()
    val isCapturingImage by viewModel.isCapturingImage.collectAsState()
    val isImportingImage by viewModel.isImportingImage.collectAsState()
    val isAnalyzingScene by viewModel.isAnalyzingScene.collectAsState()

    SharedArScreen(
        platformName = viewModel.platformName,
        selectedImageName = selectedImageName,
        selectedImagePath = selectedImagePath,
        statusMessage = statusMessage,
        primaryLabel = primaryLabel,
        detectedCandidates = detectedCandidates,
        canCaptureImages = viewModel.canCaptureImages,
        canImportImages = viewModel.canImportImages,
        isCapturingImage = isCapturingImage,
        isImportingImage = isImportingImage,
        isAnalyzingScene = isAnalyzingScene,
        onCaptureImage = viewModel::captureImage,
        onImportImage = viewModel::importImage,
        onOpenCamera = viewModel::openCamera,
        onAnalyzeScene = viewModel::analyzeScene,
        onOpenTextMode = onOpenTextMode,
        modifier = modifier
    )
}

@Composable
fun SharedArScreen(
    platformName: String,
    selectedImageName: String?,
    selectedImagePath: String?,
    statusMessage: String?,
    primaryLabel: String?,
    detectedCandidates: List<String>,
    canCaptureImages: Boolean,
    canImportImages: Boolean,
    isCapturingImage: Boolean,
    isImportingImage: Boolean,
    isAnalyzingScene: Boolean,
    onCaptureImage: () -> Unit,
    onImportImage: () -> Unit,
    onOpenCamera: () -> Unit,
    onAnalyzeScene: () -> Unit,
    onOpenTextMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .then(SmartVisionLiveScreenBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SmartVisionHeader(
                title = "AR-Modus",
                trailing = {
                    TextButton(onClick = onOpenTextMode) {
                        Text("Zum Textmodus", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            SmartVisionGlassCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Plattform: $platformName",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (canCaptureImages) {
                            "Dieser Bereich bringt den SmartVision-AR-Flow auf Bildbasis live: Objekt aufnehmen, alternativ importieren, Cloud-Erkennung starten und das Motiv direkt im Ergebnis pruefen."
                        } else {
                            "Dieser Bereich nutzt die vorhandene Kamera-Bridge und Cloud-Erkennung, um ein Objektbild auszuwerten und das erkannte Motiv sofort anzuzeigen."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    if (canCaptureImages) {
                        Button(
                            onClick = onCaptureImage,
                            enabled = !isCapturingImage,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                if (isCapturingImage) {
                                    "Kamera startet..."
                                } else {
                                    "Objekt aufnehmen"
                                }
                            )
                        }
                    } else {
                        Button(
                            onClick = onOpenCamera,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text("Kamera oeffnen")
                        }
                    }
                    if (canImportImages) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onImportImage,
                            enabled = !isImportingImage,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text(
                                if (isImportingImage) {
                                    "Bildauswahl laeuft..."
                                } else if (canCaptureImages) {
                                    "Bild aus Fotos waehlen"
                                } else {
                                    "Bild importieren"
                                }
                            )
                        }
                    }
                }
            }

            selectedImageName?.let { imageName ->
                Spacer(modifier = Modifier.height(18.dp))
                SmartVisionAccentCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = imageName,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        selectedImagePath?.let { path ->
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = path,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAnalyzeScene,
                    enabled = !isAnalyzingScene,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        if (isAnalyzingScene) {
                            "Objekterkennung laeuft..."
                        } else {
                            "Objekt im Bild erkennen"
                        }
                    )
                }
            }

            statusMessage?.let { message ->
                Spacer(modifier = Modifier.height(18.dp))
                SmartVisionStatusCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            primaryLabel?.let { label ->
                Spacer(modifier = Modifier.height(18.dp))
                SmartVisionGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Erkanntes Hauptobjekt",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color(0xFF7DEBFF),
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (detectedCandidates.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            detectedCandidates.forEach { candidate ->
                                Text(
                                    text = "• $candidate",
                                    color = Color.White.copy(alpha = 0.82f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
