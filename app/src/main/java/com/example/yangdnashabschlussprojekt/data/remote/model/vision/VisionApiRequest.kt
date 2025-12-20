package com.example.yangdnashabschlussprojekt.data.remote.model.vision

data class VisionApiRequest(
    val requests: List<AnnotateImageRequest>
)

data class AnnotateImageRequest(
    val image: Image,
    val features: List<Feature>
)

data class Image(
    val content: String // Base64 String
)

data class Feature(
    val type: String, // "LABEL_DETECTION" oder "DOCUMENT_TEXT_DETECTION"
    val maxResults: Int = 1
)

data class VisionApiResponse(
    val responses: List<AnnotateImageResponse>
)

data class AnnotateImageResponse(
    val fullTextAnnotation: FullTextAnnotation? = null,
    val labelAnnotations: List<LabelAnnotation>? = null // Das hier brauchst du für Objekte!
)

data class FullTextAnnotation(
    val text: String
)

data class LabelAnnotation(
    val description: String,
    val score: Float
)