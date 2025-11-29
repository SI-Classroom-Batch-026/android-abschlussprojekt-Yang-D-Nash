package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Base64
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.data.repository.VisionRepository
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService

class CameraXManager(
    private val context: Context,
    private val cameraExecutor: ExecutorService,
    private val visionRepository: VisionRepository,
    private val onTextDetected: (text: String, boxes: List<Rect>, bitmapWidth: Int, bitmapHeight: Int) -> Unit
) {

    var imageCapture: ImageCapture? = null
    private var lastText: String = ""

    @OptIn(ExperimentalGetImage::class)
    fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            val analyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val bitmap = mediaImage.toBitmap()
                    val base64 = bitmap.toBase64()

                    visionRepository.detectText(base64) { detectedText, boxes ->
                        if (detectedText != lastText) {
                            lastText = detectedText
                            onTextDetected(detectedText, boxes, bitmap.width, bitmap.height)
                        }
                        imageProxy.close()
                    }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as LifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                    analyzer
                )
            } catch (e: Exception) {
                Log.e("CameraXManager", "Bind failed", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    /** --- Hilfsfunktionen --- **/

    private fun Image.toBitmap(): Bitmap {
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

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
