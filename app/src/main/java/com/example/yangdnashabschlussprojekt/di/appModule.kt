package com.example.yangdnashabschlussprojekt.di

import androidx.room.Room
import com.example.yangdnashabschlussprojekt.data.local.AppDatabase
import com.example.yangdnashabschlussprojekt.data.local.database.dao.TextHistoryDao
import com.example.yangdnashabschlussprojekt.data.local.source.HistoryDataSourceImpl
import com.example.yangdnashabschlussprojekt.data.remote.api.VisionApiService
import com.example.yangdnashabschlussprojekt.data.remote.repository.UserRepository
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.data.source.IHistoryDataSource
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val VISION_BASE: String = "https://vision.googleapis.com/"
private const val API_KEY = "AIzaSyCeptnqf5FVyWnYkMzb4tRaXI8L8RY9ZcY"
val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "text_history_db"
        ).build()
    }
    single { get<AppDatabase>().textHistoryDao() }
    single<IHistoryDataSource> {
        val dao: TextHistoryDao = get()
        HistoryDataSourceImpl(dao)
    }
    single { FirebaseAuth.getInstance() }
    single { UserRepository(get()) }
    single<VisionApiService> {
        Retrofit.Builder()
            .baseUrl(VISION_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VisionApiService::class.java)
    }
    single { VisionRepository(apiKey = API_KEY, api = get()) }
    includes(viewModelModule)
}