package com.example.yangdnashabschlussprojekt.data.model.vision

import kotlinx.serialization.Serializable

@Serializable
data class VisionApiRequest(
    val requests: List<AnnotateImageRequest>
)

@Serializable
data class AnnotateImageRequest(
    val image: VisionImage,
    val features: List<VisionFeature>
)

@Serializable
data class VisionImage(
    val content: String
)

@Serializable
data class VisionFeature(
    val type: String,
    val maxResults: Int = 10
)

@Serializable
data class VisionApiResponse(
    val responses: List<AnnotateImageResponse> = emptyList()
)

@Serializable
data class AnnotateImageResponse(
    val fullTextAnnotation: FullTextAnnotation? = null,
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation> = emptyList(),
    val labelAnnotations: List<EntityAnnotation> = emptyList(),
    val logoAnnotations: List<EntityAnnotation> = emptyList(),
    val error: VisionError? = null
)

@Serializable
data class FullTextAnnotation(
    val text: String = ""
)

@Serializable
data class LocalizedObjectAnnotation(
    val name: String = "",
    val score: Float = 0f
)

@Serializable
data class EntityAnnotation(
    val description: String = "",
    val score: Float = 0f
)

@Serializable
data class VisionError(
    val code: Int? = null,
    val message: String? = null
)
