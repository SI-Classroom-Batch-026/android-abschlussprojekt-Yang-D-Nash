package com.example.yangdnashabschlussprojekt.di.sharedModul

import com.example.yangdnashabschlussprojekt.data.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    single { UserRepository() }
    single { SettingsRepository(get()) }

    viewModel {
        WelcomeViewModel(
            userRepository = get(),
            cameraManager = get(),
            settingsRepository = get()
        )
    }
}
