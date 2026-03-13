package com.example.yangdnashabschlussprojekt.ui.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset
import kotlinx.cinterop.CValue
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
import platform.AVFoundation.AVCaptureBracketedStillImageSettings
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceDiscoverySession
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureDevicePositionBack
import platform.AVFoundation.AVCaptureDeviceTypeBuiltInWideAngleCamera
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCapturePhotoOutput
import platform.AVFoundation.AVCapturePhotoCaptureDelegateProtocol
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
import platform.CoreGraphics.CGRect
import platform.CoreGraphics.CGRectMake
import platform.CoreMedia.CMSampleBufferRef
import platform.Foundation.NSError
import platform.Foundation.NSTemporaryDirectory
import platform.QuartzCore.CAShapeLayer
import platform.UIKit.UIBezierPath
import platform.UIKit.UIColor
import platform.UIKit.UIScreen
import platform.UIKit.UIViewController
import platform.Vision.VNClassifyImageRequest
import platform.Vision.VNClassificationObservation
import platform.Vision.VNGenerateAttentionBasedSaliencyImageRequest
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeAnimalsRequest
import platform.Vision.VNRecognizedObjectObservation
import platform.Vision.VNRectangleObservation
import platform.Vision.VNSaliencyImageObservation
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create

@Composable
fun rememberIOSLiveArScannerController(
    filePrefix: String,
    onCaptureResult: (Result<ImportedImageAsset?>) -> Unit
): IOSLiveArScannerController {
    return remember(filePrefix) {
        IOSLiveArScannerController(
            filePrefix = filePrefix,
            onCaptureResult = onCaptureResult
        )
    }
}

@Composable
fun IOSLiveArScannerPreview(
    controller: IOSLiveArScannerController,
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

data class IOSArTrackedBox(
    val id: Int,
    val label: String,
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
class IOSLiveArScannerController(
    private val filePrefix: String,
    private val onCaptureResult: (Result<ImportedImageAsset?>) -> Unit
) : NSObject(), AVCaptureVideoDataOutputSampleBufferDelegateProtocol, AVCapturePhotoCaptureDelegateProtocol {

    private val accentColor = UIColor.colorWithRed(0.0, 0.95, 1.0, 1.0)
    private val accentFillColor = UIColor.colorWithRed(0.0, 0.90, 1.0, 0.12)
    private val analysisQueue = dispatch_queue_create("smartvision.ios.live.ar", null)
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

    private val _livePrimaryLabel = MutableStateFlow("")
    val livePrimaryLabel: StateFlow<String> = _livePrimaryLabel.asStateFlow()

    private val _liveCandidates = MutableStateFlow<List<String>>(emptyList())
    val liveCandidates: StateFlow<List<String>> = _liveCandidates.asStateFlow()

    private val _trackedBoxes = MutableStateFlow<List<IOSArTrackedBox>>(emptyList())
    val trackedBoxes: StateFlow<List<IOSArTrackedBox>> = _trackedBoxes.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var configured = false
    private var isAnalyzingFrame = false
    private var lastFrameAnalysisEpochMillis = 0L
    private var nextTrackedBoxId = 1
    private val trackedBoxStates = mutableMapOf<Int, MutableTrackedBox>()

    val isCameraAvailable: Boolean
        get() = configured

    val viewController: UIViewController = UIViewController().apply {
        view.backgroundColor = UIColor.blackColor
        view.layer.addSublayer(previewLayer)
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

    fun release() {
        videoOutput.setSampleBufferDelegate(null, queue = null)
        if (session.running) {
            session.stopRunning()
        }
        trackedBoxStates.clear()
        _trackedBoxes.value = emptyList()
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
                            _statusMessage.value = "Bitte erlaube den Kamerazugriff fuer den Live-AR-Scanner."
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
            _statusMessage.value = "Auf diesem iOS-Geraet gibt es keine rueckseitige Kamera fuer den Live-AR-Scanner."
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
        if (!configured || isAnalyzingFrame) return

        val now = Clock.System.now().toEpochMilliseconds()
        if (now - lastFrameAnalysisEpochMillis < 450) return
        lastFrameAnalysisEpochMillis = now
        isAnalyzingFrame = true

        try {
            val classifyRequest = VNClassifyImageRequest()
            val saliencyRequest = VNGenerateAttentionBasedSaliencyImageRequest()
            val animalRequest = VNRecognizeAnimalsRequest()

            val handler = VNImageRequestHandler(sampleBuffer, emptyMap<Any?, Any>())
            handler.performRequests(listOf(classifyRequest, saliencyRequest, animalRequest), error = null)

            val animalResults = animalRequest.results
                ?.filterIsInstance<VNRecognizedObjectObservation>()
                .orEmpty()

            val classificationResults = classifyRequest.results
                ?.filterIsInstance<VNClassificationObservation>()
                .orEmpty()

            val saliencyRects = saliencyRequest.results
                ?.filterIsInstance<VNSaliencyImageObservation>()
                ?.firstOrNull()
                ?.salientObjects
                ?.filterIsInstance<VNRectangleObservation>()
                .orEmpty()

            val liveCandidates = when {
                animalResults.isNotEmpty() -> animalResults.mapNotNull { observation ->
                    observation.labels
                        .firstOrNull()
                        as? VNClassificationObservation
                }.mapNotNull { animalLabel ->
                    animalLabel.identifier
                        ?.toSmartVisionLabel()
                }

                else -> classificationResults
                    .filter { it.confidence > 0.08 }
                    .mapNotNull { it.identifier?.toSmartVisionLabel() }
            }.distinct()

            val primaryLabel = liveCandidates.firstOrNull().orEmpty()
            val liveDetections = when {
                animalResults.isNotEmpty() -> animalResults.mapNotNull { observation ->
                    val label = observation.labels
                        .firstOrNull() as? VNClassificationObservation
                    val rawLabel = label?.identifier?.toSmartVisionLabel().orEmpty()
                    buildLiveDetection(
                        normalizedRect = observation.boundingBox(),
                        label = rawLabel
                    )
                }

                saliencyRects.isNotEmpty() -> saliencyRects.mapIndexedNotNull { index, rect ->
                    buildLiveDetection(
                        normalizedRect = rect.boundingBox(),
                        label = liveCandidates.getOrNull(index)
                            ?: if (index == 0) primaryLabel else ""
                    )
                }

                else -> emptyList()
            }

            dispatch_async(dispatch_get_main_queue()) {
                _livePrimaryLabel.value = primaryLabel
                _liveCandidates.value = liveCandidates.take(5)
                _trackedBoxes.value = updateTrackedBoxes(liveDetections)
                overlayLayer.path = null
            }
        } catch (_: Throwable) {
            dispatch_async(dispatch_get_main_queue()) {
                _livePrimaryLabel.value = ""
                _liveCandidates.value = emptyList()
                trackedBoxStates.clear()
                _trackedBoxes.value = emptyList()
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

    private fun buildLiveDetection(
        normalizedRect: CValue<CGRect>,
        label: String
    ): LiveDetection {
        val rect = normalizedRect.useContents {
            NormalizedRect(
                left = origin.x.toFloat(),
                top = (1.0 - origin.y - size.height).toFloat(),
                right = (origin.x + size.width).toFloat(),
                bottom = (1.0 - origin.y).toFloat()
            )
        }

        return LiveDetection(
            label = label.trim(),
            rect = rect
        )
    }

    private fun updateTrackedBoxes(
        detections: List<LiveDetection>
    ): List<IOSArTrackedBox> {
        val now = Clock.System.now().toEpochMilliseconds()
        val smoothingFactor = 0.36f
        val retentionMillis = 900L
        val unmatchedTrackedIds = trackedBoxStates.keys.toMutableSet()

        detections.forEach { detection ->
            val bestMatchId = unmatchedTrackedIds
                .mapNotNull { trackedId ->
                    val tracked = trackedBoxStates[trackedId] ?: return@mapNotNull null
                    val score = tracked.matchScore(detection)
                    if (score < 0.08f) return@mapNotNull null
                    trackedId to score
                }
                .maxByOrNull { it.second }
                ?.first

            val trackedBox = if (bestMatchId != null) {
                unmatchedTrackedIds.remove(bestMatchId)
                trackedBoxStates.getValue(bestMatchId).apply {
                    rect = rect.smoothTowards(detection.rect, smoothingFactor)
                    if (detection.label.isNotBlank()) {
                        label = detection.label
                    }
                    lastSeenEpochMillis = now
                }
            } else {
                val newTrackedBox = MutableTrackedBox(
                    id = nextTrackedBoxId++,
                    label = detection.label,
                    rect = detection.rect,
                    lastSeenEpochMillis = now
                )
                trackedBoxStates[newTrackedBox.id] = newTrackedBox
                newTrackedBox
            }

            trackedBox.lastSeenEpochMillis = now
        }

        trackedBoxStates.entries.removeAll { (_, tracked) ->
            now - tracked.lastSeenEpochMillis > retentionMillis
        }

        return trackedBoxStates.values
            .sortedByDescending { tracked ->
                tracked.rect.area()
            }
            .map { tracked ->
                IOSArTrackedBox(
                    id = tracked.id,
                    label = tracked.label,
                    left = tracked.rect.left,
                    top = tracked.rect.top,
                    right = tracked.rect.right,
                    bottom = tracked.rect.bottom
                )
            }
    }
}

private fun String.toSmartVisionLabel(): String? {
    return substringBefore(',')
        .replace('_', ' ')
        .replace('-', ' ')
        .trim()
        .takeIf { it.isNotBlank() }
}

private data class LiveDetection(
    val label: String,
    val rect: NormalizedRect
)

private data class MutableTrackedBox(
    val id: Int,
    var label: String,
    var rect: NormalizedRect,
    var lastSeenEpochMillis: Long
) {
    fun matchScore(detection: LiveDetection): Float {
        val overlap = rect.iou(detection.rect)
        val centerDistancePenalty = rect.centerDistance(detection.rect)
        val labelBonus = when {
            label.isBlank() || detection.label.isBlank() -> 0.02f
            label.equals(detection.label, ignoreCase = true) -> 0.18f
            else -> -0.08f
        }
        return overlap - centerDistancePenalty + labelBonus
    }
}

private data class NormalizedRect(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
) {
    fun smoothTowards(
        target: NormalizedRect,
        factor: Float
    ): NormalizedRect {
        return NormalizedRect(
            left = left + ((target.left - left) * factor),
            top = top + ((target.top - top) * factor),
            right = right + ((target.right - right) * factor),
            bottom = bottom + ((target.bottom - bottom) * factor)
        )
    }

    fun area(): Float {
        return width() * height()
    }

    fun iou(other: NormalizedRect): Float {
        val intersectionLeft = maxOf(left, other.left)
        val intersectionTop = maxOf(top, other.top)
        val intersectionRight = minOf(right, other.right)
        val intersectionBottom = minOf(bottom, other.bottom)
        val intersectionWidth = (intersectionRight - intersectionLeft).coerceAtLeast(0f)
        val intersectionHeight = (intersectionBottom - intersectionTop).coerceAtLeast(0f)
        val intersectionArea = intersectionWidth * intersectionHeight
        if (intersectionArea <= 0f) return 0f
        val unionArea = area() + other.area() - intersectionArea
        return if (unionArea <= 0f) 0f else intersectionArea / unionArea
    }

    fun centerDistance(other: NormalizedRect): Float {
        val deltaX = centerX() - other.centerX()
        val deltaY = centerY() - other.centerY()
        return kotlin.math.sqrt((deltaX * deltaX) + (deltaY * deltaY))
    }

    private fun width(): Float = (right - left).coerceAtLeast(0f)
    private fun height(): Float = (bottom - top).coerceAtLeast(0f)
    private fun centerX(): Float = left + (width() / 2f)
    private fun centerY(): Float = top + (height() / 2f)
}
