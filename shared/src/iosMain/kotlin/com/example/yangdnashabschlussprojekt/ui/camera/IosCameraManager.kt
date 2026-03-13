package com.example.yangdnashabschlussprojekt.ui.camera

import com.example.yangdnashabschlussprojekt.IOSViewControllerHolder
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.datetime.Clock
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
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
        return "iOS nutzt fuer den Text-Scanner jetzt die native Aufnahme oder Foto-Auswahl. Eine Live-Kamera per AVFoundation folgt als naechster Schritt fuer den AR-Flow."
    }

    override suspend fun captureImage(): ImportedImageAsset? = presentImagePicker(
        preferredSourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera,
        fallbackSourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary,
        unavailableMessage = "Auf diesem Geraet ist weder Kamera noch Fotoauswahl verfuegbar."
    )

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
}

@OptIn(ExperimentalForeignApi::class, ExperimentalEncodingApi::class)
private object IOSImagePickerCoordinator {
    var activeDelegate: IOSImagePickerDelegate? = null
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

        if (image == null) {
            finish(picker, Result.failure(IllegalStateException("Kein Bild aus der iOS-Auswahl erhalten.")))
            return
        }

        val imageData: NSData = UIImageJPEGRepresentation(image, 0.9) ?: run {
            finish(picker, Result.failure(IllegalStateException("Bilddaten konnten auf iOS nicht gelesen werden.")))
            return
        }
        val imageBytes = imageData.toByteArray()

        val fileName = "ios-import-${Clock.System.now().toEpochMilliseconds()}.jpg"
        val absolutePath = NSTemporaryDirectory().trimEnd('/') + "/$fileName"
        val writeSucceeded = imageBytes.writeToFile(absolutePath)
        if (!writeSucceeded) {
            finish(picker, Result.failure(IllegalStateException("Importiertes iOS-Bild konnte nicht gespeichert werden.")))
            return
        }

        finish(
            picker,
            Result.success(
                ImportedImageAsset(
                    fileName = fileName,
                    absolutePath = absolutePath,
                    base64Content = Base64.Default.encode(imageBytes)
                )
            )
        )
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

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val result = ByteArray(length.toInt())
    if (result.isEmpty()) return result
    result.usePinned { pinned ->
        platform.posix.memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.writeToFile(path: String): Boolean {
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

private fun UIViewController.topMostPresentedViewController(): UIViewController {
    var current = this
    while (current.presentedViewController != null) {
        current = current.presentedViewController!!
    }
    return current
}
