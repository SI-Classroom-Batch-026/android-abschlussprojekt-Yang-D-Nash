package com.example.yangdnashabschlussprojekt.di

import android.content.Context
import com.example.yangdnashabschlussprojekt.data.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.camera.AndroidCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel<WelcomeViewModel> {
        WelcomeViewModel(
            userRepository = get<UserRepository>(),
            cameraManager = get<CameraManager>(),
            settingsRepository = get<SettingsRepository>()
        )
    }
    single<CameraManager> { AndroidCameraManager() }
    single<Settings> {
        val prefs = androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }
}