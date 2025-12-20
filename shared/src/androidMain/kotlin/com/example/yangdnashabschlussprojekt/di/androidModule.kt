package com.example.yangdnashabschlussprojekt.di

import android.content.Context
import com.example.yangdnashabschlussprojekt.ui.camera.AndroidCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val androidModule = module {
    viewModel {
        WelcomeViewModel(
            userRepository = get(),
            cameraManager = get(),      // Move this to 2nd position
            settingsRepository = get()   // Move this to 3rd position
        )
    }
    single<CameraManager> { AndroidCameraManager() }
    single<Settings> {
        val prefs = androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }
}