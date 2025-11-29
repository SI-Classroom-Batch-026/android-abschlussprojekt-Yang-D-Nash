package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.ui.viewmodel.ARViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.SettingsViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


private const val VISION_BASE: String = "https://vision.googleapis.com/"
private const val API_KEY = "AIzaSyCeptnqf5FVyWnYkMzb4tRaXI8L8RY9ZcY"
val appModule = module {

    single { FirebaseAuth.getInstance() }

    single { UserRepository(get()) }

    single<VisionApiService> {
        Retrofit.Builder()
            .baseUrl(VISION_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }

    single { VisionRepository(
        apiKey = API_KEY,
        api = get()
    ) }

    viewModelOf(::WelcomeViewModel)

    viewModelOf(::SettingsViewModel)

    viewModelOf(::ARViewModel)

    viewModelOf(::TextViewModel)

}

