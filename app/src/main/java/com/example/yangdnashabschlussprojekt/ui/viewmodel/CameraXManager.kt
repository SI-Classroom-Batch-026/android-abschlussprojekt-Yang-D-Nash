package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraXManager(
    val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) : CameraManager {
    private val mainExecutor = ContextCompat.getMainExecutor(context)
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null

    // Volatile sorgt für Thread-Sicherheit beim Modus-Wechsel
    @Volatile var isTextMode: Boolean = true
    @Volatile private var isCapturing = false

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        analyzer: ImageAnalysis.Analyzer
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            cameraProvider = providerFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                .build()
                .also { it.setAnalyzer(executor, analyzer) }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview, imageCapture, imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Binding failed", e)
            }
        }, mainExecutor)
    }

    fun captureForCloudScan(onCaptured: (String) -> Unit, onError: (Exception) -> Unit) {
        if (isCapturing) return
        val capture = imageCapture ?: return
        isCapturing = true

        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val base64 = processImageProxy(image)
                    mainExecutor.execute { onCaptured(base64) }
                } catch (e: Exception) {
                    mainExecutor.execute { onError(e) }
                } finally {
                    image.close()
                    isCapturing = false
                }
            }
            override fun onError(exc: ImageCaptureException) {
                isCapturing = false
                mainExecutor.execute { onError(exc) }
            }
        })
    }

    private fun processImageProxy(image: ImageProxy): String {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining()).apply { buffer.get(this) }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val finalBitmap = if (rotated.width > 1024) {
            val ratio = 1024f / rotated.width
            rotated.scale(1024, (rotated.height * ratio).toInt())
        } else rotated

        val out = ByteArrayOutputStream()
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
        val result = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)

        if (bitmap != rotated) bitmap.recycle()
        if (rotated != finalBitmap) rotated.recycle()
        finalBitmap.recycle()
        return result
    }

    fun unbindAll() { cameraProvider?.unbindAll() }
    override fun openCamera() {}
}