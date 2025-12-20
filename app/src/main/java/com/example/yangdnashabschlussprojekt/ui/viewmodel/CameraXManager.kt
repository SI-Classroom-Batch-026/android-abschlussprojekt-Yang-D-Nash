package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Suppress("OPT_IN_ARGUMENT_IS_NOT_MARKER")
class CameraXManager(
    val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) : CameraManager {

    private val mainExecutor = ContextCompat.getMainExecutor(context)
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // Dieser Flag steuert im laufenden Betrieb, welches ViewModel Daten kriegt
    @Volatile var isTextMode: Boolean = true
    @Volatile private var isCapturing = false

    fun interface FrameAnalyzer : ImageAnalysis.Analyzer

    override fun openCamera() {
        Log.d("CameraXManager", "Kamera-Schnittstelle aktiv.")
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
            val provider = cameraProvider ?: return@addListener

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            // Bildaufnahme konfigurieren
            @Suppress("DEPRECATION")
            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1280, 720))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            // Analyzer konfigurieren
            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analyzer?.let {
                analysis.setAnalyzer(executor, it)
                imageAnalyzer = analysis
            }

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
                onReady()
            } catch (e: Exception) {
                Log.e("CameraXManager", "Use-Case Binding fehlgeschlagen", e)
            }
        }, mainExecutor)
    }

    fun captureForCloudScan(onCaptured: (String) -> Unit, onError: (Exception) -> Unit) {
        if (isCapturing) {
            onError(IllegalStateException("Capture läuft bereits."))
            return
        }
        isCapturing = true
        val capture = imageCapture ?: run {
            onError(IllegalStateException("ImageCapture nicht bereit."))
            isCapturing = false
            return
        }

        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val base64 = image.toBase64AndRotate()
                    mainExecutor.execute { onCaptured(base64) }
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

        return ByteArrayOutputStream().use { outputStream ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
        }
    }
}