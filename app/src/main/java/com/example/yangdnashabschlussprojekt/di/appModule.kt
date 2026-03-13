package com.example.yangdnashabschlussprojekt.di

import androidx.room.Room
import com.example.yangdnashabschlussprojekt.data.companion.DesktopCompanionClient
import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository as SharedHistoryRepository
import com.example.yangdnashabschlussprojekt.data.repository.LocalHistoryStore
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository as SharedUserRepository
import com.example.yangdnashabschlussprojekt.BuildConfig
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val VISION_BASE = "https://vision.googleapis.com/"

val appModule = module {

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { OkHttpClient.Builder().build() }

    single<VisionApiService> {
        Retrofit.Builder()
            .baseUrl(VISION_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "text_history_db")
            .fallbackToDestructiveMigration(false)
            .build()
    }
    single { get<AppDatabase>().textHistoryDao() }

    single { UserRepository(firebaseAuth = get(), firestore = get(), storage = get()) }
    single { VisionRepository(apiKey = BuildConfig.CLOUD_VISION_API_KEY, api = get()) }
    single { HistoryRepository(get(), get()) }
    single<IHistoryDataSource> { HistoryDataSourceImpl(get()) }
    single { SettingsRepository(androidContext()) }
    single { DesktopCompanionClient(settingsRepository = get(), okHttpClient = get()) }
    single<LocalHistoryStore> { AndroidLocalHistoryStore(get()) }
    single { SharedUserRepository() }
    single { SharedHistoryRepository(localHistoryStore = get(), userRepository = get()) }

    factory { GetHistoryUseCase(get()) }
    factory { ManageHistoryUseCase(get()) }

    includes(viewModelModule)
}
