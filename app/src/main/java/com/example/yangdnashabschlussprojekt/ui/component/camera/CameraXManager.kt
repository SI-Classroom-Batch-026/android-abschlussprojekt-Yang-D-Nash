package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.content.Context
import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService

class CameraXManager(
    private val context: Context,
    private val cameraExecutor: ExecutorService,
    private val onTextDetected: (String, List<Rect>) -> Unit
) {
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
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
                    val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                    textRecognizer.process(inputImage)
                        .addOnSuccessListener { result ->
                            // Update nur bei Änderung
                            if (result.text != lastText) {
                                lastText = result.text
                                val boxes = result.textBlocks.flatMap { it.lines }.mapNotNull { it.boundingBox }
                                onTextDetected(result.text, boxes)
                            }
                        }
                        .addOnFailureListener { e -> Log.e("OCR", "Fehler", e) }
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                    analyzer
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Bind failed", e)
            }

        }, ContextCompat.getMainExecutor(context))
    }
}
