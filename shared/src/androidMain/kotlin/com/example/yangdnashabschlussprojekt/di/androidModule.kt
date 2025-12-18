package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.ui.camera.AndroidCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.dsl.module

val androidModule = module {
    single<CameraManager> { AndroidCameraManager() }
}