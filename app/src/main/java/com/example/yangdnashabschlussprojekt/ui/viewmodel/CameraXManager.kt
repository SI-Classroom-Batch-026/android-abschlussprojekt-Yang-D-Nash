package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraXManager(
    val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) : CameraManager {
    var isTextMode: Boolean = true
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzerCallback: ((ImageProxy) -> Unit)? = null

    fun setAnalyzer(analyzer: (ImageProxy) -> Unit) {
        this.imageAnalyzerCallback = analyzer
    }

    // Neu: Explizites Stoppen der Analyse beim Screen-Wechsel
    fun stopAnalysis() {
        imageAnalyzer?.clearAnalyzer()
        imageAnalyzerCallback = null
    }

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        baseAnalyzer: ImageAnalysis.Analyzer
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            try {
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
                    .also { analysis ->
                        analysis.setAnalyzer(executor) { imageProxy ->
                            // Sicherheitscheck: Ist der Proxy noch valide?
                            try {
                                baseAnalyzer.analyze(imageProxy)
                                imageAnalyzerCallback?.invoke(imageProxy)
                            } catch (e: Exception) {
                                Log.e("CameraXManager", "Analysis error", e)
                                imageProxy.close()
                            }
                        }
                    }

                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CameraXManager", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun captureForCloudScan(onCaptured: (String) -> Unit, onError: (Exception) -> Unit) {
        val capture = imageCapture ?: run {
            onError(Exception("Kamera nicht bereit"))
            return
        }
        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val base64 = imageProxyToBase64(image)
                    ContextCompat.getMainExecutor(context).execute { onCaptured(base64) }
                } catch (e: Exception) {
                    ContextCompat.getMainExecutor(context).execute { onError(e) }
                } finally {
                    image.close()
                }
            }
            override fun onError(exception: ImageCaptureException) {
                ContextCompat.getMainExecutor(context).execute { onError(exception) }
            }
        })
    }

    private fun imageProxyToBase64(image: ImageProxy): String {
        // Falls das Image bereits zu ist, fangen wir das hier ab
        val bitmap = try {
            val plane = image.planes[0]
            val buffer = plane.buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        } ?: return ""

        val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        val outputStream = ByteArrayOutputStream()
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    override fun openCamera() {}
}