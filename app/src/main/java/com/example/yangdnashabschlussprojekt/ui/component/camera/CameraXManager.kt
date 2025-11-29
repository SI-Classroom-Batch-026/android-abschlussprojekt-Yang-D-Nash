package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.yangdnashabschlussprojekt.data.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.data.api.VisionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import android.util.Size

class CameraXManager(
    private val context: Context,
    private val executor: Executor,
    private val repository: VisionRepository,
    private val onResult: (VisionResult) -> Unit
) {

    fun startCamera(previewView: PreviewView) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.surfaceProvider = previewView.surfaceProvider

            @Suppress("DEPRECATION") val analyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analyzer.setAnalyzer(executor) { imageProxy ->

                val bitmap = imageProxy.toBitmap()

                CoroutineScope(Dispatchers.IO).launch {
                    val result = repository.recognizeText(bitmap)
                    onResult(
                        result.copy(
                            width = bitmap.width,
                            height = bitmap.height
                        )
                    )
                }

                imageProxy.close()
            }

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as LifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analyzer
            )
        }, ContextCompat.getMainExecutor(context))
    }
}

