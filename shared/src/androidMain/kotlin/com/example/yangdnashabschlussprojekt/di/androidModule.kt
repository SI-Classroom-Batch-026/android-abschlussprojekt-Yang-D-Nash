package com.example.yangdnashabschlussprojekt.di

import android.content.Context
import com.example.yangdnashabschlussprojekt.ui.camera.AndroidCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<CameraManager> { AndroidCameraManager() }
    single<Settings> {
        val prefs = androidContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
        SharedPreferencesSettings(prefs)
    }
}
