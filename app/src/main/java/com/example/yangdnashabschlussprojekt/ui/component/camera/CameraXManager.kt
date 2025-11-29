package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraXManager(
    private val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) {

    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun startCamera(previewView: PreviewView, lifecycleOwner: LifecycleOwner, onReady: () -> Unit = {}) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider }

            @Suppress("DEPRECATION")
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(android.util.Size(1280, 720))
                .build()

            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )

            onReady()
        }, ContextCompat.getMainExecutor(context))
    }

    fun captureFrame(onCaptured: (Bitmap) -> Unit) {
        val capture = imageCapture ?: return

        val tempFile = createTempFile(context)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

        capture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val bitmap = outputFileResults.savedUri?.let { loadBitmapFromUri(context, it) }
                    ?: BitmapFactory.decodeFile(tempFile.absolutePath)
                onCaptured(bitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        })
    }
}

fun createTempFile(context: Context): File =
    File.createTempFile("capture_", ".jpg", context.cacheDir)

fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? =
    context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
