// In shared/src/androidMain/kotlin/com/example/yangdnashabschlussprojekt/di/AndroidModule.kt
package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import org.koin.dsl.module

val platformModule = module {
    single<CameraManager> { CameraXManager(get()) }
    
    single { SettingsRepository(get()) }
}