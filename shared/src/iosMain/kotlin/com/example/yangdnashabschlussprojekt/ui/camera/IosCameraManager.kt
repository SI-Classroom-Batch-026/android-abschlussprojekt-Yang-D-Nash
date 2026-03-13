package com.example.yangdnashabschlussprojekt.ui.camera

import com.example.yangdnashabschlussprojekt.IOSViewControllerHolder
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCAction
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import kotlinx.datetime.Clock
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSTemporaryDirectory
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIButton
import platform.UIKit.UIButtonTypeSystem
import platform.UIKit.UIColor
import platform.UIKit.UIControlEventTouchUpInside
import platform.UIKit.UIControlStateNormal
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UILabel
import platform.UIKit.UIScreen
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
class IOSCameraManager : CameraManager {
    override val platformName: String = "iOS"
    override val supportsDirectCapture: Boolean = true
    override val supportsImageImport: Boolean = true

    override fun openCamera(): String {
        return "iOS nutzt jetzt eine eigene native In-App-Kamera fuer Aufnahmen und weiterhin die Foto-Auswahl fuer Importe."
    }

    override suspend fun captureImage(): ImportedImageAsset? {
        val cameraSource =
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        return if (UIImagePickerController.isSourceTypeAvailable(cameraSource)) {
            presentNativeCameraCapture()
        } else {
            presentImagePicker(
                preferredSourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary,
                fallbackSourceType = null,
                unavailableMessage = "Auf diesem Geraet ist keine Kamera verfuegbar. Bitte waehle ein Bild aus deiner Mediathek."
            )
        }
    }

    override suspend fun importImage(): ImportedImageAsset? = presentImagePicker(
        preferredSourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary,
        fallbackSourceType = null,
        unavailableMessage = "Die iOS-Fotoauswahl ist auf diesem Geraet nicht verfuegbar."
    )

    private suspend fun presentImagePicker(
        preferredSourceType: UIImagePickerControllerSourceType,
        fallbackSourceType: UIImagePickerControllerSourceType?,
        unavailableMessage: String
    ): ImportedImageAsset? = suspendCancellableCoroutine { continuation ->
        val presenter = IOSViewControllerHolder.rootViewController?.topMostPresentedViewController()
        if (presenter == null) {
            continuation.resumeWithException(
                IllegalStateException("Kein iOS-ViewController fuer die Bildaufnahme verfuegbar.")
            )
            return@suspendCancellableCoroutine
        }

        val sourceType = when {
            UIImagePickerController.isSourceTypeAvailable(preferredSourceType) -> preferredSourceType
            fallbackSourceType != null &&
                UIImagePickerController.isSourceTypeAvailable(fallbackSourceType) -> fallbackSourceType
            else -> {
                continuation.resumeWithException(IllegalStateException(unavailableMessage))
                return@suspendCancellableCoroutine
            }
        }

        val delegate = IOSImagePickerDelegate { result ->
            if (continuation.isCompleted) return@IOSImagePickerDelegate
            result
                .onSuccess { continuation.resume(it) }
                .onFailure { continuation.resumeWithException(it) }
        }
        IOSImagePickerCoordinator.activeDelegate = delegate

        val picker = UIImagePickerController().apply {
            setDelegate(delegate)
            setSourceType(sourceType)
        }

        presenter.presentViewController(picker, animated = true, completion = null)

        continuation.invokeOnCancellation {
            IOSImagePickerCoordinator.activeDelegate = null
            picker.dismissViewControllerAnimated(true, completion = null)
        }
    }

    private suspend fun presentNativeCameraCapture(): ImportedImageAsset? =
        suspendCancellableCoroutine { continuation ->
            val presenter = IOSViewControllerHolder.rootViewController?.topMostPresentedViewController()
            if (presenter == null) {
                continuation.resumeWithException(
                    IllegalStateException("Kein iOS-ViewController fuer die Kamera verfuegbar.")
                )
                return@suspendCancellableCoroutine
            }

            val picker = UIImagePickerController()
            val delegate = IOSNativeCameraCoordinator(picker) { result ->
                if (continuation.isCompleted) return@IOSNativeCameraCoordinator
                result
                    .onSuccess { continuation.resume(it) }
                    .onFailure { continuation.resumeWithException(it) }
            }

            IOSImagePickerCoordinator.activeDelegate = delegate

            picker.apply {
                setDelegate(delegate)
                setSourceType(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)
                setShowsCameraControls(false)
                setAllowsEditing(false)
                cameraOverlayView = delegate.buildOverlayView()
            }

            presenter.presentViewController(picker, animated = true, completion = null)

            continuation.invokeOnCancellation {
                IOSImagePickerCoordinator.activeDelegate = null
                picker.dismissViewControllerAnimated(true, completion = null)
            }
        }
}

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
private object IOSImagePickerCoordinator {
    var activeDelegate: Any? = null
}

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
private class IOSImagePickerDelegate(
    private val onComplete: (Result<ImportedImageAsset?>) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage] as? UIImage)
            ?: (didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage)

        finish(picker, image.toImportedImageAssetResult("ios-import"))
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        finish(picker, Result.success(null))
    }

    private fun finish(
        picker: UIImagePickerController,
        result: Result<ImportedImageAsset?>
    ) {
        picker.dismissViewControllerAnimated(true) {
            IOSImagePickerCoordinator.activeDelegate = null
            onComplete(result)
        }
    }
}

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
private class IOSNativeCameraCoordinator(
    private val picker: UIImagePickerController,
    private val onComplete: (Result<ImportedImageAsset?>) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    private val accentColor = UIColor.colorWithRed(
        red = 0.0,
        green = 0.95,
        blue = 1.0,
        alpha = 1.0
    )

    fun buildOverlayView(): UIView {
        val screenBounds = UIScreen.mainScreen.bounds
        val width = screenBounds.useContents { size.width }
        val height = screenBounds.useContents { size.height }

        val overlayView = UIView(frame = screenBounds).apply {
            backgroundColor = UIColor.clearColor
        }

        val topShade = UIView(frame = CGRectMake(0.0, 0.0, width, 140.0)).apply {
            backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.28)
        }

        val titleLabel = UILabel(frame = CGRectMake(24.0, 64.0, width - 48.0, 28.0)).apply {
            text = "SmartVision Kamera"
            textColor = UIColor.whiteColor
            textAlignment = 1
            font = platform.UIKit.UIFont.boldSystemFontOfSize(22.0)
        }

        val subtitleLabel = UILabel(frame = CGRectMake(24.0, 98.0, width - 48.0, 20.0)).apply {
            text = "Objekt oder Text aufnehmen"
            textColor = UIColor.whiteColor.colorWithAlphaComponent(0.72)
            textAlignment = 1
            font = platform.UIKit.UIFont.systemFontOfSize(14.0)
        }

        val closeButton = UIButton.buttonWithType(UIButtonTypeSystem).apply {
            setFrame(CGRectMake(20.0, 58.0, 104.0, 40.0))
            setTitle("Schliessen", forState = UIControlStateNormal)
            setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
            backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.35)
            layer.cornerRadius = 20.0
            addTarget(
                target = this@IOSNativeCameraCoordinator,
                action = NSSelectorFromString("cancelCapture"),
                forControlEvents = UIControlEventTouchUpInside
            )
        }

        val captureButton = UIButton.buttonWithType(UIButtonTypeSystem).apply {
            setFrame(CGRectMake((width - 92.0) / 2.0, height - 156.0, 92.0, 92.0))
            backgroundColor = UIColor.whiteColor
            layer.cornerRadius = 46.0
            layer.borderWidth = 6.0
            layer.borderColor = accentColor.CGColor
            addTarget(
                target = this@IOSNativeCameraCoordinator,
                action = NSSelectorFromString("capturePhoto"),
                forControlEvents = UIControlEventTouchUpInside
            )
        }

        val captureInner = UIView(frame = CGRectMake(14.0, 14.0, 64.0, 64.0)).apply {
            backgroundColor = accentColor
            layer.cornerRadius = 32.0
            userInteractionEnabled = false
        }

        val hintLabel = UILabel(frame = CGRectMake(24.0, height - 52.0, width - 48.0, 20.0)).apply {
            text = "Tippe auf den Ausloeser"
            textColor = UIColor.whiteColor.colorWithAlphaComponent(0.72)
            textAlignment = 1
            font = platform.UIKit.UIFont.systemFontOfSize(14.0)
        }

        captureButton.addSubview(captureInner)
        overlayView.addSubview(topShade)
        overlayView.addSubview(titleLabel)
        overlayView.addSubview(subtitleLabel)
        overlayView.addSubview(closeButton)
        overlayView.addSubview(captureButton)
        overlayView.addSubview(hintLabel)

        return overlayView
    }

    @ObjCAction
    fun capturePhoto() {
        picker.takePicture()
    }

    @ObjCAction
    fun cancelCapture() {
        finish(Result.success(null))
    }

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage] as? UIImage)
            ?: (didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage)

        finish(image.toImportedImageAssetResult("ios-camera"))
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        finish(Result.success(null))
    }

    private fun finish(result: Result<ImportedImageAsset?>) {
        picker.dismissViewControllerAnimated(true) {
            IOSImagePickerCoordinator.activeDelegate = null
            onComplete(result)
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun UIImage?.toImportedImageAssetResult(
    filePrefix: String
): Result<ImportedImageAsset?> {
    if (this == null) {
        return Result.failure(IllegalStateException("Kein Bild aus der iOS-Kamera erhalten."))
    }

    val imageData: NSData = UIImageJPEGRepresentation(this, 0.9) ?: return Result.failure(
        IllegalStateException("Bilddaten konnten auf iOS nicht gelesen werden.")
    )
    val imageBytes = imageData.toByteArray()
    val fileName = "$filePrefix-${Clock.System.now().toEpochMilliseconds()}.jpg"
    val absolutePath = NSTemporaryDirectory().trimEnd('/') + "/$fileName"

    if (!imageBytes.writeToFile(absolutePath)) {
        return Result.failure(IllegalStateException("Das iOS-Bild konnte nicht gespeichert werden."))
    }

    return Result.success(
        ImportedImageAsset(
            fileName = fileName,
            absolutePath = absolutePath,
            base64Content = Base64.Default.encode(imageBytes)
        )
    )
}

@OptIn(ExperimentalForeignApi::class)
internal fun NSData.toByteArray(): ByteArray {
    val result = ByteArray(length.toInt())
    if (result.isEmpty()) return result
    result.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}

@OptIn(ExperimentalForeignApi::class)
internal fun ByteArray.writeToFile(path: String): Boolean {
    val file = fopen(path, "wb") ?: return false
    return try {
        usePinned { pinned ->
            fwrite(pinned.addressOf(0), 1uL, size.toULong(), file)
        }
        true
    } finally {
        fclose(file)
    }
}

internal fun UIViewController.topMostPresentedViewController(): UIViewController {
    var current = this
    while (current.presentedViewController != null) {
        current = current.presentedViewController!!
    }
    return current
}
