package com.example.yangdnashabschlussprojekt.feature.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionRepository
import com.example.yangdnashabschlussprojekt.feature.repository.CaptureGateway
import com.example.yangdnashabschlussprojekt.feature.repository.SessionGateway
import com.example.yangdnashabschlussprojekt.shared.SmartVisionStatusCard
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val AndroidScannerAccent = Color(0xFF00E5FF)

@Composable
internal fun AndroidTextScannerRoute(
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
    cloudVisionRepository: CloudVisionRepository,
    cloudTranslateRepository: CloudTranslateRepository,
    captureGateway: CaptureGateway,
    sessionGateway: SessionGateway
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val controller = remember(context) { AndroidTextScannerController(context) }
    val cameraPermission = rememberAndroidCameraPermissionState()
    val livePreviewText by controller.livePreviewText.collectAsState()
    val liveBoxes by controller.liveBoxes.collectAsState()
    val cameraStatusMessage by controller.statusMessage.collectAsState()
    val isCameraReady by controller.isCameraReady.collectAsState()
    val currentUser by sessionGateway.currentUser.collectAsState(initial = null)

    var recognizedText by remember { mutableStateOf("") }
    var translatedText by remember { mutableStateOf("") }
    var translationStatus by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var isCloudProcessing by remember { mutableStateOf(false) }
    var isLiveRecognitionActive by remember { mutableStateOf(true) }
    var showResultSheet by remember { mutableStateOf(false) }

    fun resetScanner() {
        recognizedText = ""
        translatedText = ""
        translationStatus = ""
        resultMessage = null
        isCloudProcessing = false
        showResultSheet = false
        isLiveRecognitionActive = true
        controller.setLiveRecognitionActive(true)
    }

    fun translateText(sourceText: String) {
        val cleanText = sourceText.trim()
        if (cleanText.isBlank()) {
            translatedText = ""
            translationStatus = ""
            return
        }

        scope.launch {
            translationStatus = "KI uebersetzt..."
            translatedText = cloudTranslateRepository.translate(cleanText)
                .getOrElse { cleanText }
            translationStatus = ""
        }
    }

    fun commitRecognizedText(sourceText: String) {
        val cleanText = sourceText.trim()
        if (cleanText.isBlank()) return
        recognizedText = cleanText
        showResultSheet = true
        isLiveRecognitionActive = false
        controller.setLiveRecognitionActive(false)
        translateText(cleanText)
    }

    DisposableEffect(controller) {
        onDispose {
            controller.release()
        }
    }

    DisposableEffect(cameraPermission.isGranted, controller) {
        if (!cameraPermission.isGranted) {
            controller.unbind("Kamerazugriff fehlt. Bitte erlaube die Berechtigung.")
        }
        onDispose { }
    }

    SharedTextScannerScreen(
        translationStatus = translationStatus,
        showResetAction = recognizedText.isNotBlank() && !isCloudProcessing,
        onReset = ::resetScanner,
        previewText = recognizedText.ifBlank { livePreviewText },
        showPreviewCard = (recognizedText.ifBlank { livePreviewText }).isNotBlank() && !showResultSheet,
        modifier = modifier.fillMaxSize(),
        cameraContent = {
            if (cameraPermission.isGranted) {
                AndroidTextCameraPreview(
                    controller = controller,
                    lifecycleOwner = lifecycleOwner,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                )
            }
        },
        overlayContent = {
            if (isLiveRecognitionActive && recognizedText.isBlank() && !showResultSheet) {
                SharedScannerFrameOverlay(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            if (isLiveRecognitionActive && liveBoxes.isNotEmpty() && recognizedText.isBlank()) {
                SharedTextBoundingBoxOverlay(
                    boxes = liveBoxes,
                    onBoxClicked = { box ->
                        commitRecognizedText(box.text)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        processingContent = {
            if (isCloudProcessing) {
                SharedProcessingOverlay(
                    title = "EXTRACTING DATA...",
                    accent = Color.Magenta,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        statusContent = {
            val visibleMessage = if (cameraPermission.isGranted) {
                resultMessage ?: cameraStatusMessage
            } else {
                null
            }
            if (!cameraPermission.isGranted || !isCameraReady || !visibleMessage.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    if (!cameraPermission.isGranted) {
                        SmartVisionStatusCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = if (cameraPermission.hasBeenDenied) {
                                    "Bitte erlaube den Kamerazugriff in Android, damit der Scanner starten kann."
                                } else {
                                    "Die Android-Kamera wartet auf Berechtigung."
                                },
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else if (!isCameraReady) {
                        SmartVisionStatusCard(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Die Android-Kamera wird vorbereitet.",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }

                    visibleMessage?.takeIf { it.isNotBlank() }?.let { message ->
                        SmartVisionStatusCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        },
        actionContent = {
            SharedTextActionStack(
                isLiveActive = isLiveRecognitionActive,
                onLiveToggle = {
                    if (isLiveRecognitionActive) {
                        isLiveRecognitionActive = false
                        controller.setLiveRecognitionActive(false)
                    } else {
                        resetScanner()
                    }
                },
                onSaveClick = {
                    scope.launch {
                        val saveError = captureGateway.saveCapture(recognizedText, translatedText)
                        resultMessage = saveError ?: "Verlauf gespeichert."
                    }
                },
                isSaveEnabled = currentUser != null && recognizedText.isNotBlank() && !isCloudProcessing,
                onHistoryClick = onOpenHistory,
                onCloudScanTriggered = {
                    isCloudProcessing = true
                    resultMessage = null
                    controller.captureForCloudScan(
                        onCaptured = { base64Image ->
                            scope.launch {
                                cloudVisionRepository.extractDocumentText(base64Image)
                                    .onSuccess { extractedText ->
                                        commitRecognizedText(extractedText)
                                        resultMessage = "Cloud-OCR abgeschlossen."
                                    }
                                    .onFailure { error ->
                                        resultMessage = error.message ?: "Cloud-OCR fehlgeschlagen."
                                    }
                                isCloudProcessing = false
                            }
                        },
                        onError = { error ->
                            isCloudProcessing = false
                            resultMessage = error.message ?: "Kameraaufnahme fehlgeschlagen."
                        }
                    )
                },
                isCloudScanEnabled = !isCloudProcessing && isCameraReady && cameraPermission.isGranted,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 110.dp, end = 16.dp)
            )
        },
        resultSheetContent = {
            if (showResultSheet && recognizedText.isNotBlank()) {
                SharedTextResultSheet(
                    recognizedText = recognizedText,
                    translatedText = translatedText,
                    onDismissRequest = { showResultSheet = false },
                    onUpdate = { updatedText ->
                        recognizedText = updatedText
                        translateText(updatedText)
                    },
                    onDone = { updatedText ->
                        recognizedText = updatedText
                        translateText(updatedText)
                        showResultSheet = false
                    },
                    onSave = { updatedText ->
                        recognizedText = updatedText
                        translateText(updatedText)
                        scope.launch {
                            val saveError = captureGateway.saveCapture(updatedText, translatedText)
                            resultMessage = saveError ?: "Verlauf gespeichert."
                        }
                        showResultSheet = false
                    },
                    saveEnabled = currentUser != null && recognizedText.isNotBlank(),
                    saveLabel = if (currentUser != null) {
                        "In Verlauf speichern"
                    } else {
                        "Login fuer Cloud Save noetig"
                    }
                )
            }
        }
    )
}

@Composable
private fun AndroidTextCameraPreview(
    controller: AndroidTextScannerController,
    lifecycleOwner: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
                controller.bind(this, lifecycleOwner)
            }
        },
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}

private class AndroidTextScannerController(
    private val context: Context
) {
    private val analyzerExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainExecutor = ContextCompat.getMainExecutor(context)
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val _livePreviewText = MutableStateFlow("")
    val livePreviewText: StateFlow<String> = _livePreviewText.asStateFlow()

    private val _liveBoxes = MutableStateFlow<List<SharedTextDetectionBox>>(emptyList())
    val liveBoxes: StateFlow<List<SharedTextDetectionBox>> = _liveBoxes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isCameraReady = MutableStateFlow(false)
    val isCameraReady: StateFlow<Boolean> = _isCameraReady.asStateFlow()

    private var processCameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var isAnalyzingFrame = false
    private var liveRecognitionActive = true

    fun bind(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        if (!context.hasCameraPermission()) {
            unbind("Kamerazugriff fehlt. Bitte erlaube die Berechtigung.")
            return
        }

        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener(
            {
                runCatching {
                    val cameraProvider = providerFuture.get()
                    processCameraProvider = cameraProvider

                    val preview = Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build()
                        .also { it.surfaceProvider = previewView.surfaceProvider }

                    imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(analyzerExecutor, ::analyzeFrame)
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                        imageAnalysis
                    )

                    _isCameraReady.value = true
                    _statusMessage.value = null
                }.onFailure { error ->
                    _isCameraReady.value = false
                    _statusMessage.value = error.message ?: "Android-Kamera konnte nicht gestartet werden."
                }
            },
            mainExecutor
        )
    }

    fun unbind(message: String? = null) {
        _isCameraReady.value = false
        _statusMessage.value = message
        imageAnalysis?.clearAnalyzer()
        processCameraProvider?.unbindAll()
    }

    fun setLiveRecognitionActive(active: Boolean) {
        liveRecognitionActive = active
        if (!active) {
            _livePreviewText.value = ""
            _liveBoxes.value = emptyList()
        }
    }

    fun captureForCloudScan(
        onCaptured: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val capture = imageCapture
        if (capture == null) {
            onError(IllegalStateException("Kamera ist noch nicht bereit."))
            return
        }

        capture.takePicture(
            analyzerExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val base64 = runCatching { imageProxyToBase64(image) }
                    image.close()
                    mainExecutor.execute {
                        base64.onSuccess(onCaptured).onFailure(onError)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    mainExecutor.execute { onError(exception) }
                }
            }
        )
    }

    fun release() {
        runCatching { textRecognizer.close() }
        unbind()
        analyzerExecutor.shutdown()
    }

    private fun analyzeFrame(imageProxy: ImageProxy) {
        if (!liveRecognitionActive || isAnalyzingFrame) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        isAnalyzingFrame = true
        val rotation = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        textRecognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val isRotated = rotation == 90 || rotation == 270
                val frameWidth = if (isRotated) imageProxy.height else imageProxy.width
                val frameHeight = if (isRotated) imageProxy.width else imageProxy.height

                _livePreviewText.value = visionText.text
                    .replace(Regex("(?<=\\w)\\n(?=\\w)"), " ")
                    .replace(Regex("\\n+"), "\n")
                    .trim()

                _liveBoxes.value = visionText.textBlocks.map { block ->
                    SharedTextDetectionBox(
                        id = block.hashCode().toString(),
                        text = block.text,
                        left = block.boundingBox?.left?.toFloat() ?: 0f,
                        top = block.boundingBox?.top?.toFloat() ?: 0f,
                        right = block.boundingBox?.right?.toFloat() ?: 0f,
                        bottom = block.boundingBox?.bottom?.toFloat() ?: 0f,
                        sourceWidth = frameWidth.toFloat(),
                        sourceHeight = frameHeight.toFloat(),
                        normalized = false,
                        strokeColor = AndroidScannerAccent
                    )
                }
            }
            .addOnFailureListener { error ->
                Log.e("AndroidTextScanner", "Frame analysis failed", error)
            }
            .addOnCompleteListener {
                isAnalyzingFrame = false
                imageProxy.close()
            }
    }

    private fun imageProxyToBase64(image: ImageProxy): String {
        val bitmap = if (image.format == android.graphics.ImageFormat.JPEG) {
            val buffer = image.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } else {
            val yBuffer = image.planes[0].buffer
            val uBuffer = image.planes[1].buffer
            val vBuffer = image.planes[2].buffer

            val ySize = yBuffer.remaining()
            val uSize = uBuffer.remaining()
            val vSize = vBuffer.remaining()

            val nv21 = ByteArray(ySize + uSize + vSize)
            yBuffer.get(nv21, 0, ySize)
            vBuffer.get(nv21, ySize, vSize)
            uBuffer.get(nv21, ySize + vSize, uSize)

            val yuvImage = android.graphics.YuvImage(
                nv21,
                android.graphics.ImageFormat.NV21,
                image.width,
                image.height,
                null
            )
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(android.graphics.Rect(0, 0, image.width, image.height), 70, out)
            BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size())
        }

        val matrix = Matrix().apply {
            postRotate(image.imageInfo.rotationDegrees.toFloat())
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )

        val finalOut = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, finalOut)
        return Base64.encodeToString(finalOut.toByteArray(), Base64.NO_WRAP)
    }
}
