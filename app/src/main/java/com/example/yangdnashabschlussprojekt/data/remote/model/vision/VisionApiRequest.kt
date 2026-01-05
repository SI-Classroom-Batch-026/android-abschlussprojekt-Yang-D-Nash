package com.example.yangdnashabschlussprojekt.data.remote.model.vision

data class VisionApiRequest(val requests: List<AnnotateImageRequest>)
data class AnnotateImageRequest(val image: Image, val features: List<Feature>)
data class Image(val content: String)
data class Feature(val type: String, val maxResults: Int = 10)

data class VisionApiResponse(val responses: List<AnnotateImageResponse>)

data class AnnotateImageResponse(
    val fullTextAnnotation: FullTextAnnotation? = null,
    val labelAnnotations: List<LabelAnnotation>? = null,
    val logoAnnotations: List<LogoAnnotation>? = null,
    val localizedObjectAnnotations: List<LocalizedObjectAnnotation>? = null
)

data class FullTextAnnotation(val text: String, val pages: List<Page> = emptyList())
data class Page(val width: Int, val height: Int, val blocks: List<Block> = emptyList())
data class Block(val boundingBox: CloudBoundingBox, val paragraphs: List<Paragraph> = emptyList())
data class Paragraph(val words: List<Word> = emptyList())
data class Word(val symbols: List<Symbol> = emptyList())
data class Symbol(val text: String)

data class LabelAnnotation(val description: String, val score: Float)
data class LogoAnnotation(val description: String, val score: Float)
data class LocalizedObjectAnnotation(
    val name: String,
    val score: Float,
    val boundingPoly: BoundingPoly
)
data class CloudBoundingBox(val vertices: List<Vertex>)
data class Vertex(val x: Int = 0, val y: Int = 0)

data class BoundingPoly(val normalizedVertices: List<NormalizedVertex>)
data class NormalizedVertex(val x: Float = 0f, val y: Float = 0f)