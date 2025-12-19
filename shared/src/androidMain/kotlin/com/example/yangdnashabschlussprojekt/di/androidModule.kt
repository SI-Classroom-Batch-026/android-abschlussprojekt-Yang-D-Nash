package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.ui.camera.AndroidCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val androidModule = module {
    viewModelOf(::WelcomeViewModel)
    single<CameraManager> { AndroidCameraManager() }
}