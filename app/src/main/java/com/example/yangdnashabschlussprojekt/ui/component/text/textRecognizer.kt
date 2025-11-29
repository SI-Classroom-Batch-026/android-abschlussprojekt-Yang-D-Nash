package com.example.yangdnashabschlussprojekt.ui.component.text

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executor

@OptIn(ExperimentalGetImage::class)
fun createTextAnalyzer(onTextDetected: (String) -> Unit, cameraExecutor: Executor): ImageAnalysis {
    val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    return ImageAnalysis.Builder().build().also { analysis ->
        analysis.setAnalyzer(cameraExecutor) { imageProxy: ImageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                textRecognizer.process(inputImage)
                    .addOnSuccessListener { result ->
                        onTextDetected(result.text)
                        Log.d("OCR", "Detected text: ${result.text}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("OCR", "Text recognition failed", e)
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            } else {
                imageProxy.close()
            }
        }
    }
}
