package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    viewModel: ARViewModel,
    onPreviewSizeChanged: ((Float, Float) -> Unit)? = null
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                // Image Analyzer
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .build()
                    .also { analyzer ->
                        analyzer.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                            val bitmap = imageProxy.myBitmap()
                            bitmap?.let { bmp ->
                                // Bitmap auf PreviewView-Größe skalieren
                                val scaledBitmap = Bitmap.createScaledBitmap(
                                    bmp,
                                    previewView.width.takeIf { it > 0 } ?: bmp.width,
                                    previewView.height.takeIf { it > 0 } ?: bmp.height,
                                    true
                                )
                                viewModel.analyzeFrame(bitmap = scaledBitmap)
                            }
                            imageProxy.close()
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        update = { previewView ->
            onPreviewSizeChanged?.invoke(previewView.width.toFloat(), previewView.height.toFloat())
        }
    )
}
