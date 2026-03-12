package com.example.yangdnashabschlussprojekt.ui.viewmodel.camera

data class ImportedImageAsset(
    val fileName: String,
    val absolutePath: String,
    val base64Content: String
)

interface CameraManager {
    val platformName: String
    fun openCamera(): String
    val supportsImageImport: Boolean
        get() = false

    suspend fun importImage(): ImportedImageAsset? = null
}
