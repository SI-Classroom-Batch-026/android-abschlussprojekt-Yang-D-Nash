package com.example.yangdnashabschlussprojekt.di

import androidx.room.Room
import com.example.yangdnashabschlussprojekt.data.local.AppDatabase
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.local.source.HistoryDataSourceImpl
import com.example.yangdnashabschlussprojekt.data.remote.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.source.IHistoryDataSource
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

private const val VISION_BASE = "https://vision.googleapis.com/"
private const val API_KEY = "AIzaSyCeptnqf5FVyWnYkMzb4tRaXI8L8RY9ZcY"

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "text_history_db").build()
    }
    single { HistoryRepository(get()) }
    single { get<AppDatabase>().textHistoryDao() }
    factory { ManageHistoryUseCase(get()) }
    single<IHistoryDataSource> { HistoryDataSourceImpl(get()) }
    single { UserRepository(get()) }
    single { SettingsRepository(androidContext()) }
    single { VisionRepository(apiKey = API_KEY, api = get()) }

    single<VisionApiService> {
        Retrofit.Builder()
            .baseUrl(VISION_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }

    single { FirebaseAuth.getInstance() }

    single { Executors.newSingleThreadExecutor() }
    single { CameraXManager(androidContext(), get()) }
    single<CameraManager> { get<CameraXManager>() }

    includes(viewModelModule)
}