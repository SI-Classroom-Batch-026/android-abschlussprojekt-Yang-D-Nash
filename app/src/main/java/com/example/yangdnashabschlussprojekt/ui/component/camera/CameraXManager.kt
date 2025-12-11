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
    private val mainExecutor = ContextCompat.getMainExecutor(context)

    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var lifecycleOwner: LifecycleOwner? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    @Volatile private var isCapturing = false

    fun interface FrameAnalyzer : ImageAnalysis.Analyzer


    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        analyzer: FrameAnalyzer? = null,
        onReady: () -> Unit = {}
    ) {
        this.lifecycleOwner = lifecycleOwner

        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()
            val provider = cameraProvider ?: return@addListener // Safety check

            preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            @Suppress("DEPRECATION")
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1280, 720))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            analyzer?.let {
                val analysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                analysis.setAnalyzer(executor, it)
                imageAnalyzer = analysis
            }

            val useCases = mutableListOf<UseCase>()
            preview?.let { useCases.add(it) }
            imageAnalyzer?.let { useCases.add(it) }

            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner, cameraSelector, *useCases.toTypedArray())

            onReady()

        }, mainExecutor)
    }

    private fun bindUseCases(isAnalysisMode: Boolean) {
        val provider = cameraProvider ?: return
        val owner = lifecycleOwner ?: return

        if (Thread.currentThread() != context.mainLooper.thread) {
            mainExecutor.execute { bindUseCases(isAnalysisMode) }
            return
        }

        if (isAnalysisMode) {
            imageCapture?.let { provider.unbind(it) }

            val useCases = mutableListOf<UseCase>()
            preview?.let { useCases.add(it) }
            imageAnalyzer?.let { useCases.add(it) }

            provider.bindToLifecycle(owner, cameraSelector, *useCases.toTypedArray())

        } else {
            imageAnalyzer?.let { provider.unbind(it) }

            val useCases = mutableListOf<UseCase>()
            preview?.let { useCases.add(it) }
            imageCapture?.let { useCases.add(it) }

            provider.bindToLifecycle(owner, cameraSelector, *useCases.toTypedArray())
        }
    }

    fun captureForCloudScan(onCaptured: (base64Image: String) -> Unit, onError: (Exception) -> Unit) {
        if (isCapturing) {
            onError(IllegalStateException("Capture already in progress."))
            return
        }
        isCapturing = true

        val capture = imageCapture ?: run {
            onError(IllegalStateException("ImageCapture use case not initialized."))
            isCapturing = false
            return
        }

        bindUseCases(isAnalysisMode = false)

        val tempFile = File.createTempFile("cloud_capture_", ".jpg", context.cacheDir)
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

                bindUseCases(isAnalysisMode = true)
                isCapturing = false
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                onError(exception)

                bindUseCases(isAnalysisMode = true)
                isCapturing = false
            }
        })
    }

    fun unbindAll() {
        cameraProvider?.unbindAll()
    }

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    @OptIn(ExperimentalGetImage::class)
    fun toBitmap(imageProxy: ImageProxy): Bitmap? {
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

    private fun Bitmap.toBase64(): String {
        ByteArrayOutputStream().use { outputStream ->
            this.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val byteArray = outputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? =
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
}