package com.example.yangdnashabschlussprojekt.di.sharedModul

import com.example.yangdnashabschlussprojekt.data.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    singleOf(::UserRepository)
    single { SettingsRepository(get()) }
    viewModel<WelcomeViewModel> {
        WelcomeViewModel(
            userRepository = get<UserRepository>(),
            cameraManager = get<CameraManager>(),
            settingsRepository = get<SettingsRepository>()
        )
    }
}
