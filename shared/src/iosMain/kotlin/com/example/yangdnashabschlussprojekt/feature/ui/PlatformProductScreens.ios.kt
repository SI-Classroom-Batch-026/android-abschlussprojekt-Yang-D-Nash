package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateRepository
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedArViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedCaptureViewModel
import com.example.yangdnashabschlussprojekt.shared.SmartVisionGlassCard
import com.example.yangdnashabschlussprojekt.shared.SmartVisionStatusCard
import com.example.yangdnashabschlussprojekt.ui.camera.IOSArTrackedBox
import com.example.yangdnashabschlussprojekt.ui.camera.IOSTextDetectedBox
import com.example.yangdnashabschlussprojekt.ui.camera.IOSLiveArScannerPreview
import com.example.yangdnashabschlussprojekt.ui.camera.IOSLiveTextScannerPreview
import com.example.yangdnashabschlussprojekt.ui.camera.detectObjectsWithIOSMLKit
import com.example.yangdnashabschlussprojekt.ui.camera.recognizeTextWithIOSMLKit
import com.example.yangdnashabschlussprojekt.ui.camera.rememberIOSLiveArScannerController
import com.example.yangdnashabschlussprojekt.ui.camera.rememberIOSLiveTextScannerController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private val ScannerAccent = Color(0xFF00F2FF)
private val ScannerWarning = Color(0xFFFFEB3B)
private val ScannerPanel = Color.Black.copy(alpha = 0.62f)

@Composable
actual fun PlatformArRoute(
    platformName: String,
    statusMessage: String?,
    onOpenCamera: () -> Unit,
    onOpenTextMode: () -> Unit,
    modifier: Modifier
) {
    IOSArLiveRoute(
        onOpenTextMode = onOpenTextMode,
        modifier = modifier
    )
}

@Composable
actual fun PlatformTextRoute(
    platformName: String,
    onOpenHistory: () -> Unit,
    modifier: Modifier
) {
    IOSTextLiveRoute(
        onOpenHistory = onOpenHistory,
        modifier = modifier
    )
}

@Composable
private fun IOSArLiveRoute(
    onOpenTextMode: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SharedArViewModel = koinViewModel()
) {
    val translateRepository: CloudTranslateRepository = koinInject()
    val coroutineScope = rememberCoroutineScope()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val primaryLabel by viewModel.primaryLabel.collectAsState()
    val translationStatus by viewModel.translationStatus.collectAsState()
    val detectedCandidates by viewModel.detectedCandidates.collectAsState()
    val isAnalyzingScene by viewModel.isAnalyzingScene.collectAsState()
    val liveScannerController = rememberIOSLiveArScannerController(filePrefix = "ios-ar-live") { result ->
        result
            .onSuccess { image ->
                if (image == null) {
                    viewModel.showStatusMessage("Keine Aufnahme erstellt.")
                } else {
                    coroutineScope.launch {
                        viewModel.setCapturedImage(image)
                        val localDetection = detectObjectsWithIOSMLKit(image.absolutePath)
                        if (localDetection != null && localDetection.primaryLabel.isNotBlank()) {
                            viewModel.applyLocalSceneDetection(
                                primaryLabel = localDetection.primaryLabel,
                                candidates = localDetection.candidates
                            )
                        } else {
                            viewModel.analyzeCapturedImage(image)
                        }
                    }
                }
            }
            .onFailure { error ->
                viewModel.showStatusMessage(
                    error.message ?: "Die iOS-Kamera konnte nicht ausgeloest werden."
                )
            }
    }
    val livePrimaryLabel by liveScannerController.livePrimaryLabel.collectAsState()
    val liveCandidates by liveScannerController.liveCandidates.collectAsState()
    val trackedBoxes by liveScannerController.trackedBoxes.collectAsState()
    val liveScannerStatus by liveScannerController.statusMessage.collectAsState()

    var translatedLiveLabel by rememberSaveable { mutableStateOf("") }
    var liveTranslationStatus by rememberSaveable { mutableStateOf("") }
    var lastTranslatedSource by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(livePrimaryLabel, primaryLabel) {
        if (!primaryLabel.isNullOrBlank()) {
            liveTranslationStatus = ""
            return@LaunchedEffect
        }

        val sourceLabel = livePrimaryLabel.trim()
        if (sourceLabel.isBlank()) {
            translatedLiveLabel = ""
            liveTranslationStatus = ""
            lastTranslatedSource = ""
            return@LaunchedEffect
        }

        if (sourceLabel == lastTranslatedSource && translatedLiveLabel.isNotBlank()) {
            return@LaunchedEffect
        }

        liveTranslationStatus = "KI uebersetzt..."
        translatedLiveLabel = translateRepository.translate(
            text = sourceLabel,
            sourceLanguage = "en"
        ).getOrElse { sourceLabel }
        lastTranslatedSource = sourceLabel
        liveTranslationStatus = ""
    }

    val hasCapturedResult = !primaryLabel.isNullOrBlank()
    val displayPrimaryLabel = primaryLabel
        ?.takeIf { it.isNotBlank() }
        ?: translatedLiveLabel.takeIf { it.isNotBlank() }
        ?: livePrimaryLabel.takeIf { it.isNotBlank() }
    val displayCandidates = if (hasCapturedResult) {
        detectedCandidates
    } else {
        liveCandidates.filterNot { candidate ->
            candidate.equals(livePrimaryLabel, ignoreCase = true)
        }
    }
    val bannerText = when {
        translationStatus.isNotBlank() -> translationStatus
        liveTranslationStatus.isNotBlank() -> liveTranslationStatus
        !displayPrimaryLabel.isNullOrBlank() -> displayPrimaryLabel
        else -> ""
    }
    val isBannerLoading = translationStatus.isNotBlank() || liveTranslationStatus.isNotBlank()
    val effectiveStatusMessage = statusMessage ?: liveScannerStatus
    val canTriggerPrimaryAction = !isAnalyzingScene && (liveScannerController.isCameraAvailable || hasCapturedResult)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        IOSLiveArScannerPreview(
            controller = liveScannerController,
            modifier = Modifier.fillMaxSize()
        )

        if (!hasCapturedResult && trackedBoxes.isNotEmpty()) {
            SmartVisionArTrackingOverlay(
                boxes = trackedBoxes,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isAnalyzingScene) {
            SmartVisionScanningLaserOverlay(
                laserColor = Color(0xFF00FFCC),
                modifier = Modifier.fillMaxSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                        radius = 980f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmartVisionPill(text = "AR")
                TextButton(onClick = onOpenTextMode) {
                    Text("TEXT", color = ScannerAccent, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!liveScannerController.isCameraAvailable) {
                SmartVisionStatusCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Auf diesem iOS-Geraet gibt es keine Live-Kamera. Du kannst weiter Bilder aus Fotos importieren.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            effectiveStatusMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                SmartVisionStatusCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = bannerText.isNotBlank(),
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 92.dp)
        ) {
            SmartVisionScannerBanner(
                text = bannerText,
                accent = if (isBannerLoading) ScannerWarning else ScannerAccent,
                loading = isBannerLoading
            )
        }

        AnimatedVisibility(
            visible = !displayPrimaryLabel.isNullOrBlank(),
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 18.dp, vertical = 184.dp)
        ) {
            SmartVisionArResultCard(
                primaryLabel = displayPrimaryLabel.orEmpty(),
                candidates = displayCandidates
            )
        }

        SmartVisionMiniActionButton(
            icon = Icons.Default.Image,
            label = "Fotos",
            onClick = viewModel::importImage,
            enabled = !isAnalyzingScene,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 118.dp)
        )

        if (hasCapturedResult) {
            SmartVisionScannerActionButton(
                icon = Icons.Default.Refresh,
                label = "Reset",
                onClick = viewModel::resetResults,
                enabled = canTriggerPrimaryAction,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 88.dp)
            )
        } else {
            SmartVisionHoldToScanButton(
                onTrigger = liveScannerController::capturePhoto,
                enabled = canTriggerPrimaryAction,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
            )
        }

        SmartVisionMiniActionButton(
            icon = Icons.AutoMirrored.Filled.List,
            label = "Text",
            onClick = onOpenTextMode,
            enabled = !isAnalyzingScene,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 118.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IOSTextLiveRoute(
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SharedCaptureViewModel = koinViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val recognizedText by viewModel.recognizedText.collectAsState()
    val translatedText by viewModel.translatedText.collectAsState()
    val translationStatus by viewModel.translationStatus.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val isAnalyzingImportedImage by viewModel.isAnalyzingImportedImage.collectAsState()
    val liveScannerController = rememberIOSLiveTextScannerController(
        filePrefix = "ios-text-live"
    ) { result ->
        result
            .onSuccess { image ->
                if (image == null) {
                    viewModel.showStatusMessage("Keine Aufnahme erstellt.")
                } else {
                    coroutineScope.launch {
                        viewModel.setCapturedImage(image)
                        val localResult = recognizeTextWithIOSMLKit(image.absolutePath)
                        if (localResult != null && localResult.fullText.isNotBlank()) {
                            viewModel.applyLocalRecognizedText(localResult.fullText)
                        } else {
                            viewModel.analyzeCapturedImage(image)
                        }
                    }
                }
            }
            .onFailure { error ->
                viewModel.showStatusMessage(
                    error.message ?: "Die iOS-Kamera konnte nicht ausgeloest werden."
                )
            }
    }
    val livePreviewText by liveScannerController.liveRecognizedText.collectAsState()
    val liveDetectedBoxes by liveScannerController.liveDetectedBoxes.collectAsState()
    val isLiveRecognitionActive by liveScannerController.isLiveRecognitionActive.collectAsState()
    val liveScannerStatus by liveScannerController.statusMessage.collectAsState()

    var showResultSheet by rememberSaveable { mutableStateOf(false) }
    var editableText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(recognizedText) {
        editableText = recognizedText
        if (recognizedText.isNotBlank()) {
            showResultSheet = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        IOSLiveTextScannerPreview(
            controller = liveScannerController,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.26f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.78f)
                        )
                    )
                )
        )

        if (isLiveRecognitionActive && recognizedText.isBlank() && !showResultSheet) {
            SmartVisionScannerFrameOverlay(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        if (isLiveRecognitionActive && liveDetectedBoxes.isNotEmpty() && recognizedText.isBlank()) {
            SmartVisionTextBoundingBoxOverlay(
                boxes = liveDetectedBoxes,
                onBoxClicked = { box ->
                    liveScannerController.setLiveRecognitionActive(false)
                    editableText = box.text
                    viewModel.commitRecognizedTextEdit(box.text)
                    showResultSheet = true
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmartVisionPill(text = "TEXT")
                Text(
                    text = if (liveScannerController.isCameraAvailable) "Live-OCR" else "Fotoimport",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!liveScannerController.isCameraAvailable) {
                SmartVisionStatusCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Auf diesem iOS-Geraet gibt es keine Live-Kamera. Du kannst weiter Bilder aus Fotos fuer OCR importieren.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            (statusMessage ?: liveScannerStatus)?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                SmartVisionStatusCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = translationStatus.isNotBlank(),
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 92.dp)
        ) {
            SmartVisionScannerBanner(
                text = translationStatus,
                accent = ScannerWarning,
                loading = true
            )
        }

        if (isAnalyzingImportedImage) {
            SmartVisionScanningLaserOverlay(
                laserColor = Color.Magenta,
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimatedVisibility(
            visible = recognizedText.isNotBlank() && !isAnalyzingImportedImage,
            enter = slideInVertically { -it } + fadeIn(),
            exit = slideOutVertically { -it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 148.dp)
        ) {
            OutlinedButton(
                onClick = viewModel::resetResults,
                shape = CircleShape,
                border = BorderStroke(1.dp, ScannerAccent.copy(alpha = 0.4f))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = ScannerAccent
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text("RESET", color = ScannerAccent, fontWeight = FontWeight.Bold)
            }
        }

        AnimatedVisibility(
            visible = (recognizedText.ifBlank { livePreviewText }).isNotBlank() && !showResultSheet,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 184.dp)
        ) {
            SmartVisionTextPreviewCard(
                recognizedText = recognizedText.ifBlank { livePreviewText }
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.End
        ) {
            SmartVisionHoldToScanButton(
                onTrigger = liveScannerController::capturePhoto,
                enabled = !isAnalyzingImportedImage,
                compact = true
            )
            SmartVisionMiniActionButton(
                icon = if (isLiveRecognitionActive) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                label = if (isLiveRecognitionActive) "Live" else "Fixiert",
                onClick = {
                    if (isLiveRecognitionActive) {
                        liveScannerController.setLiveRecognitionActive(false)
                    } else {
                        showResultSheet = false
                        viewModel.resetResults()
                        liveScannerController.setLiveRecognitionActive(true)
                    }
                },
                enabled = !isAnalyzingImportedImage
            )
            SmartVisionMiniActionButton(
                icon = Icons.Default.Image,
                label = "Fotos",
                onClick = viewModel::importImage,
                enabled = !isAnalyzingImportedImage
            )
            SmartVisionMiniActionButton(
                icon = Icons.Default.Save,
                label = "Save",
                onClick = viewModel::saveCapture,
                enabled = recognizedText.isNotBlank() && !isAnalyzingImportedImage
            )
            SmartVisionMiniActionButton(
                icon = Icons.AutoMirrored.Filled.List,
                label = "Verlauf",
                onClick = onOpenHistory,
                enabled = !isAnalyzingImportedImage
            )
        }

        if (showResultSheet && recognizedText.isNotBlank()) {
            SmartVisionTextResultSheet(
                recognizedText = editableText,
                translatedText = translatedText,
                onRecognizedTextChange = { editableText = it },
                onDismiss = { showResultSheet = false },
                onUpdate = { viewModel.commitRecognizedTextEdit(editableText) },
                onSave = {
                    viewModel.commitRecognizedTextEdit(editableText)
                    viewModel.saveCapture()
                    showResultSheet = false
                }
            )
        }
    }
}

@Composable
private fun SmartVisionHoldToScanButton(
    onTrigger: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val progress = remember { Animatable(0f) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val outerSize = if (compact) 82.dp else 120.dp
    val innerSize = if (compact) 56.dp else 80.dp
    val iconSize = if (compact) 22.dp else 32.dp
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.92f else 1f,
        animationSpec = tween(150),
        label = "holdToScanScale"
    )

    LaunchedEffect(isPressed, enabled) {
        if (!enabled) {
            progress.snapTo(0f)
            return@LaunchedEffect
        }

        if (isPressed) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
            )
            if (progress.value == 1f) {
                onTrigger()
                progress.snapTo(0f)
            }
        } else {
            progress.animateTo(0f, tween(250))
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(outerSize)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = if (compact) 5.dp.toPx() else 6.dp.toPx()

            drawCircle(
                color = Color.LightGray.copy(alpha = 0.18f),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
            )
            if (enabled) {
                drawArc(
                    color = if (progress.value >= 1f) Color.Green else ScannerAccent,
                    startAngle = -90f,
                    sweepAngle = 360f * progress.value,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        Surface(
            onClick = { },
            enabled = enabled,
            interactionSource = interactionSource,
            shape = CircleShape,
            color = when {
                !enabled -> Color(0xFF2C2C2E)
                isPressed -> ScannerAccent
                else -> Color.White.copy(alpha = 0.14f)
            },
            shadowElevation = if (isPressed && enabled) 0.dp else 6.dp,
            modifier = Modifier
                .size(innerSize)
                .scale(buttonScale)
                .then(
                    if (enabled) {
                        Modifier.indication(
                            interactionSource = interactionSource,
                            indication = ripple(
                                bounded = false,
                                radius = innerSize / 2,
                                color = ScannerAccent
                            )
                        )
                    } else {
                        Modifier
                    }
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Scan",
                    modifier = Modifier.size(iconSize),
                    tint = when {
                        !enabled -> Color.White.copy(alpha = 0.45f)
                        isPressed -> Color.Black
                        else -> Color.White
                    }
                )
            }
        }
    }
}

@Composable
private fun SmartVisionScannerFrameOverlay(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val color = ScannerAccent.copy(alpha = 0.45f)
        val strokeWidth = 1.5.dp.toPx()
        val cornerLength = 30.dp.toPx()
        val width = size.width
        val height = size.height

        drawPath(
            path = Path().apply {
                moveTo(0f, cornerLength)
                lineTo(0f, 0f)
                lineTo(cornerLength, 0f)
            },
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
        )

        drawPath(
            path = Path().apply {
                moveTo(width - cornerLength, 0f)
                lineTo(width, 0f)
                lineTo(width, cornerLength)
            },
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
        )

        drawPath(
            path = Path().apply {
                moveTo(0f, height - cornerLength)
                lineTo(0f, height)
                lineTo(cornerLength, height)
            },
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
        )

        drawPath(
            path = Path().apply {
                moveTo(width - cornerLength, height)
                lineTo(width, height)
                lineTo(width, height - cornerLength)
            },
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(strokeWidth)
        )
    }
}

@Composable
private fun SmartVisionScanningLaserOverlay(
    laserColor: Color,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "smartVisionLaser")
    val progress by transition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing)
        ),
        label = "laserProgress"
    )

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .align(Alignment.TopCenter)
                .padding(top = (progress * 720f).dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to laserColor.copy(alpha = 0.72f),
                        0.51f to Color.White,
                        0.52f to laserColor.copy(alpha = 0.72f),
                        1f to Color.Transparent
                    )
                )
        )
    }
}

@Composable
private fun SmartVisionPill(text: String) {
    Surface(
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.58f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = ScannerAccent,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun SmartVisionScannerBanner(
    text: String,
    accent: Color,
    loading: Boolean
) {
    Surface(
        color = if (loading) ScannerWarning.copy(alpha = 0.92f) else ScannerPanel,
        shape = CircleShape,
        border = BorderStroke(1.dp, accent.copy(alpha = if (loading) 0f else 0.26f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.size(12.dp))
            }
            Text(
                text = text.uppercase(),
                color = if (loading) Color.Black else accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun SmartVisionArResultCard(
    primaryLabel: String,
    candidates: List<String>
) {
    SmartVisionGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 220.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "TARGET ACQUIRED",
                style = MaterialTheme.typography.labelSmall,
                color = ScannerAccent,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = primaryLabel.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.ExtraBold
            )
            if (candidates.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                candidates.take(5).forEach { candidate ->
                    Text(
                        text = "• $candidate",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun SmartVisionArTrackingOverlay(
    boxes: List<IOSArTrackedBox>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = TextStyle(
        color = Color.Black,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    Canvas(modifier = modifier) {
        val strokePx = 2.5.dp.toPx()
        val bracketSize = 20.dp.toPx()

        boxes.forEach { box ->
            val left = box.left * size.width
            val top = box.top * size.height
            val right = box.right * size.width
            val bottom = box.bottom * size.height
            val hudColor = ScannerAccent

            drawLine(hudColor, Offset(left, top), Offset(left + bracketSize, top), strokePx)
            drawLine(hudColor, Offset(left, top), Offset(left, top + bracketSize), strokePx)

            drawLine(hudColor, Offset(right, top), Offset(right - bracketSize, top), strokePx)
            drawLine(hudColor, Offset(right, top), Offset(right, top + bracketSize), strokePx)

            drawLine(hudColor, Offset(left, bottom), Offset(left + bracketSize, bottom), strokePx)
            drawLine(hudColor, Offset(left, bottom), Offset(left, bottom - bracketSize), strokePx)

            drawLine(hudColor, Offset(right, bottom), Offset(right - bracketSize, bottom), strokePx)
            drawLine(hudColor, Offset(right, bottom), Offset(right, bottom - bracketSize), strokePx)

            if (box.label.isNotBlank()) {
                val textLayoutResult = textMeasurer.measure(box.label.uppercase(), labelStyle)
                val textWidth = textLayoutResult.size.width.toFloat()
                val textHeight = textLayoutResult.size.height.toFloat()
                val padding = 8f
                val topOffset = (top - textHeight - padding).coerceAtLeast(8f)

                drawRect(
                    color = hudColor,
                    topLeft = Offset(left, topOffset),
                    size = Size(textWidth + (padding * 2), textHeight + padding)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = box.label.uppercase(),
                    style = labelStyle,
                    topLeft = Offset(left + padding, topOffset + (padding / 2))
                )
            }
        }
    }
}

@Composable
private fun SmartVisionTextBoundingBoxOverlay(
    boxes: List<IOSTextDetectedBox>,
    onBoxClicked: (IOSTextDetectedBox) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "textOverlayPulse")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing)
        ),
        label = "textOverlayAlpha"
    )

    Canvas(
        modifier = modifier.pointerInput(boxes) {
            detectTapGestures { tapOffset ->
                boxes.forEach { box ->
                    val left = box.left * size.width
                    val top = box.top * size.height
                    val right = box.right * size.width
                    val bottom = box.bottom * size.height

                    if (
                        tapOffset.x in left..right &&
                        tapOffset.y in top..bottom
                    ) {
                        onBoxClicked(box)
                        return@detectTapGestures
                    }
                }
            }
        }
    ) {
        boxes.forEach { box ->
            val left = box.left * size.width
            val top = box.top * size.height
            val right = box.right * size.width
            val bottom = box.bottom * size.height

            drawRect(
                color = ScannerAccent.copy(alpha = 0.1f),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top)
            )
            drawRect(
                color = ScannerAccent.copy(alpha = alpha),
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            )
        }
    }
}

@Composable
private fun SmartVisionTextPreviewCard(
    recognizedText: String
) {
    SmartVisionGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 200.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "FULL SCAN RESULTS",
                style = MaterialTheme.typography.labelSmall,
                color = ScannerAccent,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = recognizedText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SmartVisionTextResultSheet(
    recognizedText: String,
    translatedText: String,
    onRecognizedTextChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    onSave: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121212),
        scrimColor = Color.Black.copy(alpha = 0.72f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "TRANSLATION",
                color = Color.Magenta,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = translatedText.ifBlank { "Uebersetzung wird vorbereitet..." },
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "ORIGINAL ANPASSEN",
                color = Color.White.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = recognizedText,
                onValueChange = onRecognizedTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                label = { Text("Erkannter Text") }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onUpdate,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
                ) {
                    Text("Fertig", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onSave,
                enabled = recognizedText.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ScannerAccent, contentColor = Color.Black)
            ) {
                Text(
                    text = "In Verlauf speichern",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SmartVisionProcessingOverlay(
    title: String,
    accent: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.82f)),
        contentAlignment = Alignment.Center
    ) {
        SmartVisionGlassCard(
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = accent,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.size(14.dp))
                Text(
                    text = title.uppercase(),
                    color = accent,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SmartVisionMiniActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = if (enabled) ScannerPanel else Color(0xFF2C2C2E),
            contentColor = Color.White,
            modifier = Modifier.size(58.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) Color.White else Color.White.copy(alpha = 0.45f)
            )
        }
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = if (enabled) 0.78f else 0.42f),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun SmartVisionScannerActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(118.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(3.dp, ScannerAccent.copy(alpha = if (enabled) 0.34f else 0.16f)),
                modifier = Modifier.fillMaxSize()
            ) {}

            FloatingActionButton(
                onClick = onClick,
                containerColor = if (enabled) Color.White else Color.White.copy(alpha = 0.42f),
                contentColor = Color.Black,
                modifier = Modifier.size(84.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(34.dp),
                    tint = ScannerAccent
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label.uppercase(),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
