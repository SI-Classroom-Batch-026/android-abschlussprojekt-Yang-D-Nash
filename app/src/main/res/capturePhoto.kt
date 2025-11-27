private fun capturePhoto(
    imageCapture: ImageCapture?,
    analyzerViewModel: AnalyzerViewModel,
    context: android.content.Context
) {
    val capture = imageCapture ?: return

    capture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: androidx.camera.core.ImageProxy) {
                val bitmap = imageProxy.toBitmap()
                imageProxy.close()
                val base64 = bitmap.toBase64()
                analyzerViewModel.analyzeImage(base64)
            }

            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}
