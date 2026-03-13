package com.example.yangdnashabschlussprojekt.ui.camera

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitViewController
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset
import platform.UIKit.UIColor
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraDevice
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerEditedImage
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

@Composable
fun rememberIOSInlineCameraController(
    filePrefix: String,
    onCaptureResult: (Result<ImportedImageAsset?>) -> Unit
): IOSInlineCameraController {
    return remember(filePrefix) {
        IOSInlineCameraController(
            filePrefix = filePrefix,
            onCaptureResult = onCaptureResult
        )
    }
}

@Composable
fun IOSInlineCameraPreview(
    controller: IOSInlineCameraController,
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

class IOSInlineCameraController(
    private val filePrefix: String,
    private val onCaptureResult: (Result<ImportedImageAsset?>) -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {

    private val cameraPicker: UIImagePickerController? =
        if (
            UIImagePickerController.isSourceTypeAvailable(
                UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            )
        ) {
            UIImagePickerController().apply {
                setSourceType(
                    UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
                )
                setShowsCameraControls(false)
                setAllowsEditing(false)
                setCameraDevice(
                    UIImagePickerControllerCameraDevice.UIImagePickerControllerCameraDeviceRear
                )
                setDelegate(this@IOSInlineCameraController)
            }
        } else {
            null
        }

    val isCameraAvailable: Boolean = cameraPicker != null

    val viewController: UIViewController = cameraPicker ?: UIViewController().apply {
        view.backgroundColor = UIColor.blackColor
    }

    fun capturePhoto() {
        val picker = cameraPicker
        if (picker == null) {
            onCaptureResult(
                Result.failure(
                    IllegalStateException(
                        "Auf diesem iOS-Geraet ist keine Live-Kamera verfuegbar."
                    )
                )
            )
            return
        }

        picker.takePicture()
    }

    fun release() {
        cameraPicker?.setDelegate(null)
    }

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = (didFinishPickingMediaWithInfo[UIImagePickerControllerEditedImage] as? UIImage)
            ?: (didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage)

        onCaptureResult(image.toImportedImageAssetResult(filePrefix))
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        onCaptureResult(Result.success(null))
    }
}
