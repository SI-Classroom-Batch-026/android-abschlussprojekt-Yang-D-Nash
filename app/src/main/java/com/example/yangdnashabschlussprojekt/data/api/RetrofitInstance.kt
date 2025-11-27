package com.example.yangdnashabschlussprojekt.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://vision.googleapis.com/"

    val api: CloudApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Wichtig für @Body
            .build()
            .create(CloudApi::class.java)
    }
}
