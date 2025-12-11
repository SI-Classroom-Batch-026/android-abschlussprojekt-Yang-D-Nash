package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.YuvImage
import android.net.Uri
import android.util.Base64
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
class CameraXManager(
    val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) {
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null

    fun interface FrameAnalyzer : ImageAnalysis.Analyzer


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
                val analysis: ImageAnalysis = ImageAnalysis.Builder()
                    // Strategie, um nur den neuesten Frame zu analysieren
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(executor, it)

                imageAnalyzer = analysis
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

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    fun toBitmap(imageProxy: ImageProxy): Bitmap? {
        // Logik zur YUV-zu-Bitmap-Konvertierung
        val image = imageProxy.image ?: return null
        if (image.format != ImageFormat.YUV_420_888) return null

        val yBuffer = image.planes[0].buffer
        val vuBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)
        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, yuvImage.width, yuvImage.height), 90, out)
        val imageBytes = out.toByteArray()
        val rotatedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        val matrix = Matrix()
        matrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

        return Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.width, rotatedBitmap.height, matrix, true)
    }
    fun captureFrameAsBase64(onCaptured: (base64Image: String) -> Unit, onError: (Exception) -> Unit) {
        val capture = imageCapture ?: run {
            onError(IllegalStateException("ImageCapture use case not initialized."))
            return
        }

        val tempFile = File.createTempFile("capture_", ".jpg", context.cacheDir)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
        capture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val bitmap = outputFileResults.savedUri?.let { loadBitmapFromUri(it) }

                if (bitmap != null) {
                    val base64 = bitmap.toBase64()
                    onCaptured(base64)
                } else {
                    onError(IllegalStateException("Failed to load captured Bitmap."))
                }

                tempFile.delete()
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onError(exception)
            }
        })
    }
    private fun Bitmap.toBase64(): String {
        ByteArrayOutputStream().use { outputStream ->
            this.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val byteArray = outputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
    }

    fun loadBitmapFromUri(uri: Uri): Bitmap? =
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }

}