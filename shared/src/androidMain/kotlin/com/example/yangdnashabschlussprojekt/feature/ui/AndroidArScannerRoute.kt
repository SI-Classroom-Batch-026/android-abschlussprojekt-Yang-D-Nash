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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionRepository
import com.example.yangdnashabschlussprojekt.shared.SmartVisionStatusCard
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private val AndroidArAccent = Color(0xFF00E5FF)
private val AndroidArCloudAccent = Color(0xFF00FFCC)

@Composable
internal fun AndroidArScannerRoute(
    onOpenTextMode: () -> Unit,
    modifier: Modifier = Modifier,
    cloudVisionRepository: CloudVisionRepository,
    cloudTranslateRepository: CloudTranslateRepository
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val controller = remember(context) { AndroidArScannerController(context) }
    val cameraPermission = rememberAndroidCameraPermissionState()
    val liveBoxes by controller.liveBoxes.collectAsState()
    val livePrimaryLabel by controller.livePrimaryLabel.collectAsState()
    val liveCandidates by controller.liveCandidates.collectAsState()
    val cameraStatusMessage by controller.statusMessage.collectAsState()
    val isCameraReady by controller.isCameraReady.collectAsState()

    var capturedPrimaryLabel by rememberSaveable { mutableStateOf<String?>(null) }
    var capturedCandidates by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var translatedLiveLabel by rememberSaveable { mutableStateOf("") }
    var liveTranslationStatus by rememberSaveable { mutableStateOf("") }
    var lastTranslatedSource by rememberSaveable { mutableStateOf("") }
    var statusMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isCloudLoading by rememberSaveable { mutableStateOf(false) }

    val hasCapturedResult = !capturedPrimaryLabel.isNullOrBlank()
    val displayPrimaryLabel = capturedPrimaryLabel
        ?.takeIf { it.isNotBlank() }
        ?: translatedLiveLabel.takeIf { it.isNotBlank() }
        ?: livePrimaryLabel.takeIf { it.isNotBlank() }
    val displayCandidates = if (hasCapturedResult) {
        capturedCandidates
    } else {
        liveCandidates.filterNot { it.equals(livePrimaryLabel, ignoreCase = true) }
    }

    fun resetScanner() {
        capturedPrimaryLabel = null
        capturedCandidates = emptyList()
        translatedLiveLabel = ""
        liveTranslationStatus = ""
        lastTranslatedSource = ""
        statusMessage = null
        isCloudLoading = false
    }

    LaunchedEffect(livePrimaryLabel, capturedPrimaryLabel) {
        if (!capturedPrimaryLabel.isNullOrBlank()) {
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
        translatedLiveLabel = cloudTranslateRepository.translate(sourceLabel, sourceLanguage = "en")
            .getOrElse { sourceLabel }
        lastTranslatedSource = sourceLabel
        liveTranslationStatus = ""
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

    SharedArScannerScreen(
        bannerText = when {
            liveTranslationStatus.isNotBlank() -> liveTranslationStatus
            !displayPrimaryLabel.isNullOrBlank() -> displayPrimaryLabel
            else -> ""
        },
        isBannerLoading = liveTranslationStatus.isNotBlank(),
        showScanning = isCloudLoading,
        showResetAction = hasCapturedResult,
        onReset = ::resetScanner,
        onScan = {
            isCloudLoading = true
            statusMessage = null
            controller.captureForCloudScan(
                onCaptured = { base64Image ->
                    scope.launch {
                        cloudVisionRepository.detectScene(base64Image)
                            .onSuccess { detection ->
                                val translatedLabel = cloudTranslateRepository.translate(
                                    text = detection.primaryLabel,
                                    sourceLanguage = "en"
                                ).getOrElse { detection.primaryLabel }
                                capturedPrimaryLabel = translatedLabel
                                capturedCandidates = detection.candidates
                                statusMessage = "Cloud-Objekterkennung abgeschlossen."
                            }
                            .onFailure { error ->
                                statusMessage = error.message ?: "Objekterkennung fehlgeschlagen."
                            }
                        isCloudLoading = false
                    }
                },
                onError = { error ->
                    isCloudLoading = false
                    statusMessage = error.message ?: "Kameraaufnahme fehlgeschlagen."
                }
            )
        },
        scanEnabled = !isCloudLoading && isCameraReady && cameraPermission.isGranted,
        modifier = modifier.fillMaxSize(),
        cameraContent = {
            if (cameraPermission.isGranted) {
                AndroidArCameraPreview(
                    controller = controller,
                    lifecycleOwner = lifecycleOwner,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize())
            }
        },
        overlayContent = {
            if (!hasCapturedResult && liveBoxes.isNotEmpty()) {
                SharedArTrackingOverlay(
                    boxes = liveBoxes,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        statusContent = {
            val visibleMessage = if (cameraPermission.isGranted) {
                statusMessage ?: cameraStatusMessage
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
        extraContent = {
            if (hasCapturedResult && !displayPrimaryLabel.isNullOrBlank()) {
                SmartVisionStatusCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 20.dp, vertical = 150.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TARGET ACQUIRED",
                            color = AndroidArCloudAccent,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = displayPrimaryLabel,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        displayCandidates.take(5).forEach { candidate ->
                            Text(
                                text = "• $candidate",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun AndroidArCameraPreview(
    controller: AndroidArScannerController,
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
        modifier = modifier.fillMaxSize()
    )
}

private class AndroidArScannerController(
    private val context: Context
) {
    private val analyzerExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val mainExecutor = ContextCompat.getMainExecutor(context)
    private val objectDetector = ObjectDetection.getClient(
        ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()
    )

    private val _liveBoxes = MutableStateFlow<List<SharedArTrackingBox>>(emptyList())
    val liveBoxes: StateFlow<List<SharedArTrackingBox>> = _liveBoxes.asStateFlow()

    private val _livePrimaryLabel = MutableStateFlow("")
    val livePrimaryLabel: StateFlow<String> = _livePrimaryLabel.asStateFlow()

    private val _liveCandidates = MutableStateFlow<List<String>>(emptyList())
    val liveCandidates: StateFlow<List<String>> = _liveCandidates.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isCameraReady = MutableStateFlow(false)
    val isCameraReady: StateFlow<Boolean> = _isCameraReady.asStateFlow()

    private var processCameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var isAnalyzingFrame = false

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
        runCatching { objectDetector.close() }
        unbind()
        analyzerExecutor.shutdown()
    }

    private fun analyzeFrame(imageProxy: ImageProxy) {
        if (isAnalyzingFrame) {
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

        objectDetector.process(inputImage)
            .addOnSuccessListener { detectedObjects ->
                val isRotated = rotation == 90 || rotation == 270
                val frameWidth = if (isRotated) imageProxy.height else imageProxy.width
                val frameHeight = if (isRotated) imageProxy.width else imageProxy.height

                val boxes = detectedObjects.mapIndexed { index, obj ->
                    val rect = obj.boundingBox
                    val label = obj.labels.firstOrNull()?.text?.uppercase() ?: "SCANNING..."
                    SharedArTrackingBox(
                        id = (obj.trackingId ?: index).toString(),
                        label = label,
                        left = rect.left.toFloat(),
                        top = rect.top.toFloat(),
                        right = rect.right.toFloat(),
                        bottom = rect.bottom.toFloat(),
                        sourceWidth = frameWidth.toFloat(),
                        sourceHeight = frameHeight.toFloat(),
                        strokeColor = AndroidArAccent
                    )
                }

                _liveBoxes.value = boxes
                _livePrimaryLabel.value = boxes.firstOrNull()?.label.orEmpty()
                _liveCandidates.value = boxes.map { it.label }.distinct()
            }
            .addOnFailureListener { error ->
                Log.e("AndroidArScanner", "Frame analysis failed", error)
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
