package com.example.yangdnashabschlussprojekt.ui.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCapturePhoto
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
import platform.AVFoundation.AVCaptureBracketedStillImageSettings
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoSettings
import platform.AVFoundation.AVCaptureResolvedPhotoSettings
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetPhoto
import platform.AVFoundation.AVCaptureVideoDataOutput
import platform.AVFoundation.AVCaptureVideoDataOutputSampleBufferDelegateProtocol
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVCaptureVideoPreviewLayer.Companion.layerWithSession
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreGraphics.CGRectMake
import platform.CoreMedia.CMSampleBufferRef
import platform.Foundation.NSError
import platform.Foundation.NSTemporaryDirectory
import platform.QuartzCore.CAShapeLayer
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIScreen
import platform.UIKit.UIViewController
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelFast
import platform.Vision.VNImageRequestHandler
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

@Composable
fun rememberIOSLiveTextScannerController(
    filePrefix: String,
    onCaptureResult: (Result<ImportedImageAsset?>) -> Unit
): IOSLiveTextScannerController {
    return remember(filePrefix) {
        IOSLiveTextScannerController(
            filePrefix = filePrefix,
            onCaptureResult = onCaptureResult
        )
    }
}

@Composable
fun IOSLiveTextScannerPreview(
    controller: IOSLiveTextScannerController,
    modifier: Modifier = Modifier
) {
    UIKitViewController(
        factory = { controller.viewController },
        modifier = modifier,
        onRelease = { controller.release() },
        properties = UIKitInteropProperties(
            isInteractive = false,
            isNativeAccessibilityEnabled = false
        )
    )
}

data class IOSTextDetectedBox(
    val text: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
class IOSLiveTextScannerController(
    private val filePrefix: String,
    private val onCaptureResult: (Result<ImportedImageAsset?>) -> Unit
) : NSObject(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol, AVCapturePhotoCaptureDelegateProtocol {

    private val accentColor = UIColor.colorWithRed(0.0, 0.95, 1.0, 1.0)
    private val accentFillColor = UIColor.colorWithRed(0.0, 0.95, 1.0, 0.16)
    private val analysisQueue = dispatch_queue_create("smartvision.ios.live.text", null)
    private val session = AVCaptureSession()
    private val photoOutput = AVCapturePhotoOutput()
    private val videoOutput = AVCaptureVideoDataOutput()
    private val previewBounds = UIScreen.mainScreen.bounds
    private val overlayLayer = CAShapeLayer().apply {
        frame = previewBounds
        strokeColor = accentColor.CGColor
        fillColor = accentFillColor.CGColor
        lineWidth = 2.0
    }
    private val previewLayer = AVCaptureVideoPreviewLayer.layerWithSession(session).apply {
        frame = previewBounds
        videoGravity = AVLayerVideoGravityResizeAspectFill
    }

    private val _liveRecognizedText = MutableStateFlow("")
    val liveRecognizedText: StateFlow<String> = _liveRecognizedText.asStateFlow()

    private val _liveDetectedBoxes = MutableStateFlow<List<IOSTextDetectedBox>>(emptyList())
    val liveDetectedBoxes: StateFlow<List<IOSTextDetectedBox>> = _liveDetectedBoxes.asStateFlow()

    private val _isLiveRecognitionActive = MutableStateFlow(true)
    val isLiveRecognitionActive: StateFlow<Boolean> = _isLiveRecognitionActive.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var configured = false
    private var isAnalyzingFrame = false
    private var lastFrameAnalysisEpochMillis = 0L

    val isCameraAvailable: Boolean
        get() = configured

    val viewController: UIViewController = UIViewController().apply {
        view.backgroundColor = UIColor.blackColor
        view.layer.addSublayer(previewLayer)
        view.layer.addSublayer(overlayLayer)
    }

    init {
        requestPermissionAndStart()
    }

    fun capturePhoto() {
        if (!configured) {
            onCaptureResult(
                Result.failure(
                    IllegalStateException(
                        _statusMessage.value ?: "Die Live-Kamera ist auf diesem iOS-Geraet noch nicht bereit."
                    )
                )
            )
            return
        }

        val settings = AVCapturePhotoSettings.photoSettings()
        photoOutput.capturePhotoWithSettings(settings, delegate = this)
    }

    fun setLiveRecognitionActive(active: Boolean) {
        _isLiveRecognitionActive.value = active
        if (!active) {
            _liveRecognizedText.value = ""
            _liveDetectedBoxes.value = emptyList()
            overlayLayer.path = null
            _statusMessage.value = "Live-Textscan pausiert."
        } else if (configured) {
            _statusMessage.value = null
        }
    }

    fun release() {
        videoOutput.setSampleBufferDelegate(null, queue = null)
        if (session.running) {
            session.stopRunning()
        }
    }

    private fun requestPermissionAndStart() {
        when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
            AVAuthorizationStatusAuthorized -> configureAndStartSession()
            AVAuthorizationStatusNotDetermined -> {
                AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                    dispatch_async(dispatch_get_main_queue()) {
                        if (granted) {
                            configureAndStartSession()
                        } else {
                            _statusMessage.value = "Bitte erlaube den Kamerazugriff fuer den Live-Textscanner."
                        }
                    }
                }
            }

            AVAuthorizationStatusDenied -> {
                _statusMessage.value = "Der Kamerazugriff ist auf iOS deaktiviert."
            }

            else -> {
                _statusMessage.value = "Die Live-Kamera ist auf diesem iOS-Geraet nicht verfuegbar."
            }
        }
    }

    private fun configureAndStartSession() {
        if (configured) return

        val cameraDevice = AVCaptureDeviceDiscoverySession.discoverySessionWithDeviceTypes(
            deviceTypes = listOf(AVCaptureDeviceTypeBuiltInWideAngleCamera),
            mediaType = AVMediaTypeVideo,
            position = AVCaptureDevicePositionBack
        ).devices.firstOrNull() as? AVCaptureDevice

        if (cameraDevice == null) {
            _statusMessage.value = "Auf diesem iOS-Geraet gibt es keine rueckseitige Kamera fuer den Live-Textscanner."
            return
        }

        val cameraInput = AVCaptureDeviceInput(device = cameraDevice, error = null)
        if (cameraInput == null) {
            _statusMessage.value = "Die iOS-Kamera konnte nicht initialisiert werden."
            return
        }

        session.beginConfiguration()
        session.sessionPreset = AVCaptureSessionPresetPhoto

        if (session.canAddInput(cameraInput)) {
            session.addInput(cameraInput)
        }

        videoOutput.alwaysDiscardsLateVideoFrames = true
        videoOutput.setSampleBufferDelegate(this, analysisQueue)
        if (session.canAddOutput(videoOutput)) {
            session.addOutput(videoOutput)
        }
        if (session.canAddOutput(photoOutput)) {
            session.addOutput(photoOutput)
        }

        session.commitConfiguration()
        configured = true
        session.startRunning()
        _statusMessage.value = null
    }

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputSampleBuffer: CMSampleBufferRef?,
        fromConnection: AVCaptureConnection
    ) {
        val sampleBuffer = didOutputSampleBuffer ?: return
        if (!configured || !_isLiveRecognitionActive.value || isAnalyzingFrame) return

        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastFrameAnalysisEpochMillis < 350) return
        lastFrameAnalysisEpochMillis = now
        isAnalyzingFrame = true

        try {
            val request = VNRecognizeTextRequest()
            request.recognitionLevel = VNRequestTextRecognitionLevelFast
            request.usesLanguageCorrection = false

            val handler = VNImageRequestHandler(sampleBuffer, emptyMap<Any?, Any>())
            handler.performRequests(listOf(request), error = null)

            val observations = request.results
                ?.filterIsInstance<VNRecognizedTextObservation>()
                .orEmpty()

            val recognizedLines = observations.mapNotNull { observation ->
                val candidate = observation.topCandidates(1u).firstOrNull()
                    as? platform.Vision.VNRecognizedText
                candidate?.string?.trim()?.takeIf { it.isNotBlank() }
            }
            val detectedBoxes = observations.mapNotNull { observation ->
                val candidate = observation.topCandidates(1u).firstOrNull()
                    as? platform.Vision.VNRecognizedText
                val text = candidate?.string?.trim()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val normalizedRect = observation.boundingBox()
                normalizedRect.useContents {
                    IOSTextDetectedBox(
                        text = text,
                        left = origin.x.toFloat(),
                        top = (1.0 - origin.y - size.height).toFloat(),
                        right = (origin.x + size.width).toFloat(),
                        bottom = (1.0 - origin.y).toFloat()
                    )
                }
            }

            dispatch_async(dispatch_get_main_queue()) {
                _liveRecognizedText.value = recognizedLines
                    .distinct()
                    .joinToString(separator = "\n")
                    .trim()
                _liveDetectedBoxes.value = detectedBoxes
                renderObservationBoxes(observations)
            }
        } catch (_: Throwable) {
            dispatch_async(dispatch_get_main_queue()) {
                _liveRecognizedText.value = ""
                _liveDetectedBoxes.value = emptyList()
                overlayLayer.path = null
            }
        } finally {
            isAnalyzingFrame = false
        }
    }

    override fun captureOutput(
        output: AVCapturePhotoOutput,
        didFinishProcessingPhotoSampleBuffer: CMSampleBufferRef?,
        previewPhotoSampleBuffer: CMSampleBufferRef?,
        resolvedSettings: AVCaptureResolvedPhotoSettings,
        bracketSettings: AVCaptureBracketedStillImageSettings?,
        error: NSError?
    ) {
        if (error != null) {
            onCaptureResult(
                Result.failure(
                    IllegalStateException(error.localizedDescription ?: "Fotoaufnahme fehlgeschlagen.")
                )
            )
            return
        }

        val photoSampleBuffer = didFinishProcessingPhotoSampleBuffer ?: run {
            onCaptureResult(Result.failure(IllegalStateException("Keine Fotodaten aus der iOS-Kamera erhalten.")))
            return
        }

        val imageData = AVCapturePhotoOutput.JPEGPhotoDataRepresentationForJPEGSampleBuffer(
            JPEGSampleBuffer = photoSampleBuffer,
            previewPhotoSampleBuffer = previewPhotoSampleBuffer
        )
        if (imageData == null) {
            onCaptureResult(Result.failure(IllegalStateException("Keine Bilddaten aus der iOS-Kamera erhalten.")))
            return
        }

        val bytes = imageData.toByteArray()
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val fileName = "$filePrefix-$timestamp.jpg"
        val absolutePath = NSTemporaryDirectory().trimEnd('/') + "/$fileName"
        if (!bytes.writeToFile(absolutePath)) {
            onCaptureResult(Result.failure(IllegalStateException("Das iOS-Bild konnte nicht gespeichert werden.")))
            return
        }
        val imageAsset = ImportedImageAsset(
            fileName = fileName,
            absolutePath = absolutePath,
            base64Content = Base64.Default.encode(bytes)
        )

        onCaptureResult(Result.success(imageAsset))
    }

    private fun renderObservationBoxes(observations: List<VNRecognizedTextObservation>) {
        if (observations.isEmpty()) {
            overlayLayer.path = null
            return
        }

        val previewWidth = previewBounds.useContents { size.width }
        val previewHeight = previewBounds.useContents { size.height }
        val path = UIBezierPath.bezierPath()

        observations.take(8).forEach { observation ->
            val normalizedRect = observation.boundingBox()
            val rect = normalizedRect.useContents {
                val width = size.width * previewWidth
                val height = size.height * previewHeight
                val x = origin.x * previewWidth
                val y = (1.0 - origin.y - size.height) * previewHeight
                CGRectMake(x, y, width, height)
            }
            path.appendPath(UIBezierPath.bezierPathWithRect(rect))
        }

        overlayLayer.path = path.CGPath
    }
}
