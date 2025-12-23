package com.example.yangdnashabschlussprojekt.di

import androidx.room.Room
import com.example.yangdnashabschlussprojekt.data.local.AppDatabase
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.local.source.HistoryDataSourceImpl
import com.example.yangdnashabschlussprojekt.data.remote.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.domain.usecase.GetHistoryUseCase
import com.example.yangdnashabschlussprojekt.domain.usecase.IHistoryDataSource
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

private const val VISION_BASE = "https://vision.googleapis.com/"
private const val API_KEY = "AIzaSyCeptnqf5FVyWnYkMzb4tRaXI8L8RY9ZcY"

val appModule = module {

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    single<VisionApiService> {
        Retrofit.Builder()
            .baseUrl(VISION_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "text_history_db")
            .fallbackToDestructiveMigration(false) // Auf true/false prüfen je nach Entwicklungsstand
            .build()
    }
    single { get<AppDatabase>().textHistoryDao() }

    single { UserRepository(firebaseAuth = get()) }
    single { VisionRepository(apiKey = API_KEY, api = get()) }
    single { HistoryRepository(get(), get()) }
    single<IHistoryDataSource> { HistoryDataSourceImpl(get()) }
    single { SettingsRepository(androidContext()) }

    factory { GetHistoryUseCase(get()) }
    factory { ManageHistoryUseCase(get(), get()) }

    single { Executors.newSingleThreadExecutor() }

    single { CameraXManager(androidContext(), get()) }

    single<CameraManager> { get<CameraXManager>() }

    includes(viewModelModule)
}