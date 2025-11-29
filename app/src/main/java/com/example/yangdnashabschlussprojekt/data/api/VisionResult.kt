package com.example.yangdnashabschlussprojekt.data.api

import android.graphics.Rect
import com.example.yangdnashabschlussprojekt.data.model.VisionResponse

data class VisionResult(
    val text: String,
    val boxes: List<Rect>,
    val width: Int,
    val height: Int
) {
    companion object {
        fun from(response: VisionResponse): VisionResult {
            val textAnnotations = response.responses.firstOrNull()?.textAnnotations ?: emptyList()

            val text = textAnnotations.firstOrNull()?.description ?: ""

            val boxes = textAnnotations.drop(1).mapNotNull { annotation ->
                val vertices = annotation.boundingPoly?.vertices
                if (vertices != null && vertices.size == 4) {
                    Rect(
                        vertices[0].x ?: 0,
                        vertices[0].y ?: 0,
                        vertices[2].x ?: 0,
                        vertices[2].y ?: 0
                    )
                } else null
            }

            return VisionResult(text, boxes, 1, 1)
        }
    }
}