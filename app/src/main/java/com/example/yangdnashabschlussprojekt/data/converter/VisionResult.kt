package com.example.yangdnashabschlussprojekt.data.converter

import android.graphics.Rect
import com.example.yangdnashabschlussprojekt.data.model.VisionResponse

data class VisionResult(
    val text: String,
    val boxes: List<Rect>
) {
    companion object {
        fun from(response: VisionResponse): VisionResult {
            val textAnnotations = response.responses.firstOrNull()?.textAnnotations
            val text = textAnnotations?.firstOrNull()?.description ?: ""

            val boxes = textAnnotations?.drop(1)?.mapNotNull { annotation ->
                annotation.boundingPoly?.let { poly ->
                    val left = poly.vertices.minOf { it.x ?: 0 }  // jetzt Int
                    val top = poly.vertices.minOf { it.y ?: 0 }
                    val right = poly.vertices.maxOf { it.x ?: 0 }
                    val bottom = poly.vertices.maxOf { it.y ?: 0 }
                    Rect(left, top, right, bottom)
                }
            } ?: emptyList()

            return VisionResult(text, boxes)
        }
    }
}
