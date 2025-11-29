package com.example.yangdnashabschlussprojekt.data.model

data class VisionResponse(
    val responses: List<VisionAnnotationResponse>
)

data class VisionAnnotationResponse(
    val textAnnotations: List<VisionTextAnnotation>?
)

data class VisionTextAnnotation(
    val description: String?,
    val boundingPoly: BoundingPoly?
)

data class BoundingPoly(
    val vertices: List<Vertex>
)

data class Vertex(
    val x: Int?,
    val y: Int?
)
