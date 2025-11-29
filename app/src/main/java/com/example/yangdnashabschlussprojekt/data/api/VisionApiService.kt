package com.example.yangdnashabschlussprojekt.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VisionApiService {

    @POST("v1/images:annotate")
    suspend fun analyzeImage(
        @Query("key") apiKey: String,
        @Body request: VisionRequest
    ): VisionResponse
}