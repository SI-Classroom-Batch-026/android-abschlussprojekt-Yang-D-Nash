package com.example.yangdnashabschlussprojekt.data.remote.api

import com.example.yangdnashabschlussprojekt.data.remote.model.vision.VisionApiRequest
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.VisionApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VisionApiService {


    @POST("v1/images:annotate")
    suspend fun annotateImage(
        @Query("key") apiKey: String,
        @Body request: VisionApiRequest
    ): VisionApiResponse
}