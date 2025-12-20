package com.example.yangdnashabschlussprojekt.ui.camera

import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.dsl.module

class IOSCameraManager : CameraManager {
    override fun openCamera() {  }
}

val platformModule = module {
    single<CameraManager> { IOSCameraManager() }
}