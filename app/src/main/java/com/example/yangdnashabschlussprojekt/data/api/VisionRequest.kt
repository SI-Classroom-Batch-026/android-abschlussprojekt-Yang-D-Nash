package com.example.yangdnashabschlussprojekt.data.api

data class VisionRequest(
    val requests: List<VisionImageRequest>
) {
    companion object {
        fun createLabelDetection(base64: String): VisionRequest {
            return VisionRequest(
                requests = listOf(
                    VisionImageRequest(
                        image = VisionImageContent(content = base64),
                        features = listOf(VisionFeature(type = "LABEL_DETECTION", maxResults = 10))
                    )
                )
            )
        }
    }
}

data class VisionImageRequest(
    val image: VisionImageContent,
    val features: List<VisionFeature>
)

data class VisionImageContent(
    val content: String
)

data class VisionFeature(
    val type: String,
    val maxResults: Int
)
