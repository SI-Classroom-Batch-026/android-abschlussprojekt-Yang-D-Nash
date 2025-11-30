package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraXManager(
    val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) {

    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null

    interface FrameAnalyzer : ImageAnalysis.Analyzer {
        fun analyzeFrame(bitmap: Bitmap)
    }

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        analyzer: FrameAnalyzer? = null,
        onReady: () -> Unit = {}
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()

            preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }


            @Suppress("DEPRECATION")
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1280, 720))
                .build()

            analyzer?.let {
                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(executor, it)
                    }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val useCases = mutableListOf<UseCase>()
            preview?.let { useCases.add(it) }
            imageCapture?.let { useCases.add(it) }
            imageAnalyzer?.let { useCases.add(it) }

            cameraProvider?.unbindAll()
            cameraProvider?.bindToLifecycle(lifecycleOwner, cameraSelector, *useCases.toTypedArray())

            onReady()
        }, ContextCompat.getMainExecutor(context))
    }

    fun captureFrame(onCaptured: (bitmap: Bitmap, rotation: Int) -> Unit) {
        val capture = imageCapture ?: return
        val tempFile = File.createTempFile("capture_", ".jpg", context.cacheDir)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
        capture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val bitmap = outputFileResults.savedUri?.let { loadBitmapFromUri(it) }
                    ?: BitmapFactory.decodeFile(tempFile.absolutePath)

                val rotationDegrees = capture.targetRotation.toDegrees()
                onCaptured(bitmap, rotationDegrees)

                tempFile.delete()
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        })
    }

    private fun Int.toDegrees(): Int = when (this) {
        android.view.Surface.ROTATION_0 -> 0
        android.view.Surface.ROTATION_90 -> 90
        android.view.Surface.ROTATION_180 -> 180
        android.view.Surface.ROTATION_270 -> 270
        else -> 0
    }

    fun loadBitmapFromUri(uri: Uri): Bitmap? =
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
}
