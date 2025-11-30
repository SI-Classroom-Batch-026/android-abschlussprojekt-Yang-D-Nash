package com.example.yangdnashabschlussprojekt.ui.component.text

import android.graphics.Rect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class TextAnalyzer(
    private val onResult: (List<Rect>) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(image: ImageProxy) {
        // TODO: hier OCR / Text-Erkennung einfügen
        onResult(emptyList())

        image.close()
    }
}
