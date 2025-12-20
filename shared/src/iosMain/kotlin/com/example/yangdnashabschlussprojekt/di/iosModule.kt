package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.ui.camera.IOSCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.dsl.module

class IOSCameraManager : CameraManager {
    override fun openCamera() {
    }
}

val iosModule = module {
    single<CameraManager> { IOSCameraManager() }
}