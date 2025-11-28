package com.example.yangdnashabschlussprojekt.data.api

data class VisionResponse(
    val responses: List<VisionAnnotateResult>
)

data class VisionAnnotateResult(
    val labelAnnotations: List<VisionLabel>? = null
)

data class VisionLabel(
    val description: String,
    val score: Float
)
