package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionConfig
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionRepository
import com.example.yangdnashabschlussprojekt.data.repository.CloudVisionTransport
import com.example.yangdnashabschlussprojekt.feature.repository.demoFeatureModule
import com.example.yangdnashabschlussprojekt.ui.camera.DesktopCameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.dsl.module

val desktopModule = module {
    includes(demoFeatureModule("Desktop"))
    single<CloudVisionConfig> { DesktopCloudVisionConfig() }
    single<CloudVisionTransport> { DesktopCloudVisionTransport() }
    single { CloudVisionRepository(config = get(), transport = get()) }
    single<CameraManager> { DesktopCameraManager() }
}
