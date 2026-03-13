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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedCaptureViewModel
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
fun SharedCaptureRoute(
    onBack: (() -> Unit)? = null,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SharedCaptureViewModel = koinViewModel()
) {
    val recognizedText by viewModel.recognizedText.collectAsState()
    val translatedText by viewModel.translatedText.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val selectedImageName by viewModel.selectedImageName.collectAsState()
    val selectedImagePath by viewModel.selectedImagePath.collectAsState()
    val isCapturingImage by viewModel.isCapturingImage.collectAsState()
    val isImportingImage by viewModel.isImportingImage.collectAsState()
    val isAnalyzingImportedImage by viewModel.isAnalyzingImportedImage.collectAsState()

    SharedCaptureScreen(
        platformName = viewModel.platformName,
        recognizedText = recognizedText,
        translatedText = translatedText,
        statusMessage = statusMessage,
        selectedImageName = selectedImageName,
        selectedImagePath = selectedImagePath,
        canCaptureImages = viewModel.canCaptureImages,
        canImportImages = viewModel.canImportImages,
        isCapturingImage = isCapturingImage,
        isImportingImage = isImportingImage,
        isAnalyzingImportedImage = isAnalyzingImportedImage,
        onRecognizedTextChange = viewModel::updateRecognizedText,
        onTranslatedTextChange = viewModel::updateTranslatedText,
        onCaptureImage = viewModel::captureImage,
        onOpenCamera = viewModel::openCamera,
        onImportImage = viewModel::importImage,
        onAnalyzeImage = viewModel::analyzeImportedImage,
        onSave = viewModel::saveCapture,
        onOpenHistory = onOpenHistory,
        onBack = onBack,
        modifier = modifier
    )
}

@Composable
fun SharedCaptureScreen(
    platformName: String,
    recognizedText: String,
    translatedText: String,
    statusMessage: String?,
    selectedImageName: String?,
    selectedImagePath: String?,
    canCaptureImages: Boolean,
    canImportImages: Boolean,
    isCapturingImage: Boolean,
    isImportingImage: Boolean,
    isAnalyzingImportedImage: Boolean,
    onRecognizedTextChange: (String) -> Unit,
    onTranslatedTextChange: (String) -> Unit,
    onCaptureImage: () -> Unit,
    onOpenCamera: () -> Unit,
    onImportImage: () -> Unit,
    onAnalyzeImage: () -> Unit,
    onSave: () -> Unit,
    onOpenHistory: () -> Unit,
    onBack: (() -> Unit)? = null,
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
                title = "Text-Scanner",
                onBack = onBack
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
                            "Dieser Bereich bildet den SmartVision-Textfluss ab: Foto aufnehmen, alternativ Bilder waehlen, OCR ausloesen, Text pruefen und im Verlauf sichern."
                        } else {
                            "Dieser Bereich bildet den SmartVision-Textfluss ab: Kamera oeffnen, Bild importieren, OCR ausloesen, Text pruefen und im Verlauf sichern."
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
                                    "Foto aufnehmen"
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
                    onClick = onAnalyzeImage,
                    enabled = !isAnalyzingImportedImage,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        if (isAnalyzingImportedImage) {
                            "Cloud OCR laeuft..."
                        } else {
                            "Cloud OCR aus Bild starten"
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

            Spacer(modifier = Modifier.height(18.dp))

            SmartVisionGlassCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Text erfassen",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = recognizedText,
                        onValueChange = onRecognizedTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Erkannter Text") },
                        minLines = 4,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = translatedText,
                        onValueChange = onTranslatedTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Uebersetzung") },
                        minLines = 4,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onSave,
                        enabled = recognizedText.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("In Verlauf speichern")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Verlauf ansehen")
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
