package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Base64
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class CameraXManager(
    private val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) : CameraManager {

    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null

    /**
     * Stoppt die aktuelle Analyse komplett und gibt Ressourcen frei.
     * Wichtig beim Wechsel zwischen Text- und AR-Screen.
     */
    fun stopAnalysis() {
        imageAnalyzer?.clearAnalyzer()
        Log.d("CameraXManager", "Analysis stopped and cleared")
    }

    /**
     * Startet die Kamera.
     * @param analyzer Der Analyzer (z.B. ARViewModel oder TextViewModel),
     * der die Frames direkt verarbeitet.
     */
    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        analyzer: ImageAnalysis.Analyzer? = null
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            try {
                cameraProvider = providerFuture.get()

                // 1. Preview Setup
                @Suppress("DEPRECATION") val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                // 2. Image Capture Setup (für Cloud Scans)
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                // 3. Image Analysis Setup
                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also { analysis ->
                        analyzer?.let {
                            analysis.setAnalyzer(executor, it)
                        }
                    }

                // 4. Bind to Lifecycle
                cameraProvider?.unbindAll()

                val useCases = mutableListOf(preview, imageCapture!!)
                imageAnalyzer?.let { useCases.add(it) }

                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    *useCases.toTypedArray()
                )

                Log.d("CameraXManager", "Camera successfully bound. Analyzer active: ${analyzer != null}")

            } catch (e: Exception) {
                Log.e("CameraXManager", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    /**
     * Erstellt ein Foto für den Cloud Scan und wandelt es in Base64 um.
     */
    fun captureForCloudScan(onCaptured: (String) -> Unit, onError: (Exception) -> Unit) {
        val capture = imageCapture ?: run {
            onError(Exception("Kamera nicht bereit"))
            return
        }

        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // .use schließt das ImageProxy automatisch am Ende des Blocks (wichtig für Buffer!)
                image.use { proxy ->
                    try {
                        val base64 = imageProxyToBase64(proxy)
                        ContextCompat.getMainExecutor(context).execute {
                            onCaptured(base64)
                        }
                    } catch (e: Exception) {
                        Log.e("CameraXManager", "Base64 conversion failed", e)
                        ContextCompat.getMainExecutor(context).execute { onError(e) }
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraXManager", "Capture failed: ${exception.message}")
                ContextCompat.getMainExecutor(context).execute { onError(exception) }
            }
        })
    }

    /**
     * Konvertiert ein ImageProxy effizient in einen Base64 String.
     */
    private fun imageProxyToBase64(image: ImageProxy): String {
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 70, out)

        val imageBytes = out.toByteArray()
        var bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // Rotation korrigieren
        if (image.imageInfo.rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(image.imageInfo.rotationDegrees.toFloat()) }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }

        val finalOut = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, finalOut)
        return Base64.encodeToString(finalOut.toByteArray(), Base64.NO_WRAP)
    }

    override fun openCamera() {
        // Implementierung falls über Interface nötig
    }
}