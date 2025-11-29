package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.concurrent.ExecutorService

class CameraXManager(
    private val context: Context,
    private val cameraExecutor: ExecutorService,
    private val onTextDetected: (String) -> Unit
) {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    var imageCapture: ImageCapture? = null

    @OptIn(ExperimentalGetImage::class)
    fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageCapture = ImageCapture.Builder().build()

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        textRecognizer.process(inputImage)
                            .addOnSuccessListener { result ->
                                onTextDetected(result.text)
                            }
                            .addOnFailureListener { e -> Log.e("OCR", "Fehler", e) }
                            .addOnCompleteListener { imageProxy.close() }
                    }
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CameraX", "Bind failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(saveFile: File, onSaved: (Uri) -> Unit, onError: (Exception) -> Unit) {
        val outputOptions = ImageCapture.OutputFileOptions.Builder(saveFile).build()
        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) = onError(exc)
                override fun onImageSaved(output: ImageCapture.OutputFileResults) = onSaved(Uri.fromFile(saveFile))
            }
        )
    }
}
