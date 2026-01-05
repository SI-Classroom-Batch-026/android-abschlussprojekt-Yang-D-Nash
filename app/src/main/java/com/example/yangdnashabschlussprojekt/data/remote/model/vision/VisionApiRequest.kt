package com.example.yangdnashabschlussprojekt.data.remote.model.vision

data class VisionApiRequest(
    val requests: List<AnnotateImageRequest>
)
data class AnnotateImageRequest(
    val image: Image,
    val features: List<Feature>
)
data class Image(
    val content: String
)
data class Feature(
    val type: String,
    val maxResults: Int = 50
)
data class VisionApiResponse(
    val responses: List<AnnotateImageResponse>
)
data class AnnotateImageResponse(
    val fullTextAnnotation: FullTextAnnotation? = null,
    val labelAnnotations: List<LabelAnnotation>? = null
)
data class FullTextAnnotation(
    val text: String,
    val pages: List<Page> = emptyList()
)
data class Page(
    val width: Int,
    val height: Int,
    val blocks: List<Block> = emptyList()
)
data class Block(
    val boundingBox: CloudBoundingBox,
    val text: String? = null
)
data class CloudBoundingBox(
    val vertices: List<Vertex>
)
data class Vertex(
    val x: Int = 0,
    val y: Int = 0
)
data class LabelAnnotation(
    val description: String,
    val score: Float
)