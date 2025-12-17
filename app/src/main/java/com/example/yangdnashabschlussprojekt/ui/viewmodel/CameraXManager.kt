package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
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
import androidx.core.graphics.scale
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
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
        val providerFuture = ProcessCameraProvider.Companion.getInstance(context)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()
            val provider = cameraProvider ?: return@addListener
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
            val useCasesToBind = mutableListOf<UseCase>()
            preview?.let { useCasesToBind.add(it) }
            imageCapture?.let { useCasesToBind.add(it) }
            imageAnalyzer?.let { useCasesToBind.add(it) }
            provider.unbindAll()
            provider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                *useCasesToBind.toTypedArray()
            )
            onReady()
        }, mainExecutor)
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
        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val base64 = image.toBase64AndRotate()
                    mainExecutor.execute {
                        onCaptured(base64)
                    }
                } catch (e: Exception) {
                    mainExecutor.execute { onError(e) }
                } finally {
                    image.close()
                    isCapturing = false
                }
            }
            override fun onError(exception: ImageCaptureException) {
                mainExecutor.execute { onError(exception) }
                isCapturing = false
            }
        })
    }
    fun unbindAll() {
        cameraProvider?.unbindAll()
    }
    @OptIn(ExperimentalGetImage::class)
    private fun ImageProxy.toBase64AndRotate(): String {
        val buffer = this.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix().apply {
            postRotate(this@toBase64AndRotate.imageInfo.rotationDegrees.toFloat())
        }
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )
        return rotatedBitmap.toBase64()
    }
    private fun Bitmap.toBase64(): String {
        val scaledBitmap = if (this.width > 1024) {
            val ratio = 1024.0 / this.width
            this.scale(1024, (this.height * ratio).toInt())
        } else {
            this
        }
        ByteArrayOutputStream().use { outputStream ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        }
    }
}