package com.example.yangdnashabschlussprojekt.data.repository

import com.example.yangdnashabschlussprojekt.data.api.VisionRequest
import com.example.yangdnashabschlussprojekt.data.api.VisionResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VisionApiService {

    @POST("v1/images:annotate")
    suspend fun analyze(
        @Query("key") apiKey: String,
        @Body request: VisionRequest
    ): VisionResponse
}
