package com.example.yangdnashabschlussprojekt.data.repository

import android.graphics.Bitmap
import android.util.Base64
import com.example.yangdnashabschlussprojekt.data.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.model.VisionFeature
import com.example.yangdnashabschlussprojekt.data.model.VisionImage
import com.example.yangdnashabschlussprojekt.data.model.VisionImageRequest
import com.example.yangdnashabschlussprojekt.data.model.VisionRequest
import com.example.yangdnashabschlussprojekt.data.api.VisionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class VisionRepository(
    private val apiKey: String,
    private val api: VisionApiService
) {

    suspend fun recognizeText(bitmap: Bitmap): VisionResult = withContext(Dispatchers.IO) {

        val base64 = bitmap.toBase64()

        val request = VisionRequest(
            requests = listOf(
                VisionImageRequest(
                    image = VisionImage(content = base64),
                    features = listOf(VisionFeature(type = "TEXT_DETECTION"))
                )
            )
        )

        val response = api.analyzeImage(apiKey, request)
        VisionResult.from(response)
    }
}

fun Bitmap.toBase64(): String {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
