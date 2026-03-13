package com.example.yangdnashabschlussprojekt.di

import android.content.Context
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudTranslateTransport
import com.example.yangdnashabschlussprojekt.data.repository.TargetLanguageProvider
import com.example.yangdnashabschlussprojekt.ui.camera.AndroidCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.Locale

val androidModule = module {
    single<CameraManager> { AndroidCameraManager() }
    single<Settings> {
        val prefs = androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }
    single<CloudTranslateConfig> { AndroidCloudTranslateConfig() }
    single<CloudTranslateTransport> { AndroidCloudTranslateTransport() }
    single<TargetLanguageProvider> { AndroidTargetLanguageProvider() }
    single { CloudTranslateRepository(config = get(), transport = get(), targetLanguageProvider = get()) }
}

class AndroidCloudTranslateConfig : CloudTranslateConfig {
    override fun apiKey(): String? = null
}

class AndroidCloudTranslateTransport : CloudTranslateTransport {
    override suspend fun postJson(url: String, body: String): String {
        throw IllegalStateException("Cloud Translate ist im Android-Shared-Layer nicht aktiv.")
    }
}

class AndroidTargetLanguageProvider : TargetLanguageProvider {
    override fun currentLanguageCode(): String = Locale.getDefault().language.ifBlank { "en" }
}
