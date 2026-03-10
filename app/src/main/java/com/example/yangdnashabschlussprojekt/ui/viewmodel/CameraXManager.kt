package com.example.yangdnashabschlussprojekt.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.util.Log
import androidx.camera.core.AspectRatio
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
    private val context: Context,
    private val executor: Executor = Executors.newSingleThreadExecutor()
) : CameraManager {

    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null

    fun startCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        analyzer: ImageAnalysis.Analyzer? = null
    ) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener({
            try {
                cameraProvider = providerFuture.get()
                @Suppress("DEPRECATION") val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also { analysis ->
                        analyzer?.let {
                            analysis.setAnalyzer(executor, it)
                        }
                    }

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

    fun captureForCloudScan(onCaptured: (String) -> Unit, onError: (Exception) -> Unit) {
        val capture = imageCapture ?: run {
            onError(Exception("Kamera nicht bereit"))
            return
        }

        capture.takePicture(executor, object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
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
                Log.e("CameraXManager", "Capture failed", exception)
                ContextCompat.getMainExecutor(context).execute { onError(exception) }
            }
        })
    }
    private fun imageProxyToBase64(image: ImageProxy): String {
        return try {
            val bitmap = if (image.format == android.graphics.ImageFormat.JPEG) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else {
                val yBuffer = image.planes[0].buffer
                val uBuffer = image.planes[1].buffer
                val vBuffer = image.planes[2].buffer

                val ySize = yBuffer.remaining()
                val uSize = uBuffer.remaining()
                val vSize = vBuffer.remaining()

                val nv21 = ByteArray(ySize + uSize + vSize)
                yBuffer.get(nv21, 0, ySize)
                vBuffer.get(nv21, ySize, vSize)
                uBuffer.get(nv21, ySize + vSize, uSize)

                val yuvImage = android.graphics.YuvImage(
                    nv21, android.graphics.ImageFormat.NV21,
                    image.width, image.height, null
                )
                val out = ByteArrayOutputStream()
                yuvImage.compressToJpeg(android.graphics.Rect(0, 0, image.width, image.height), 70, out)
                val imageBytes = out.toByteArray()
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            }

            val matrix = Matrix().apply {
                postRotate(image.imageInfo.rotationDegrees.toFloat())
            }
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )

            val finalOut = ByteArrayOutputStream()
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, finalOut)
            Base64.encodeToString(finalOut.toByteArray(), Base64.NO_WRAP)

        } catch (e: Exception) {
            Log.e("CameraXManager", "Conversion failed", e)
            ""
        }
    }

    override fun openCamera() {
        Log.d("CameraXManager", "openCamera() called but is not wired to a UI PreviewView. Use startCamera() in your Activity/Fragment.")
    }
}