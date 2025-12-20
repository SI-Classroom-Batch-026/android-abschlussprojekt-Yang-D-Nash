package com.example.yangdnashabschlussprojekt.di.sharedModul

import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.domain.usecase.GetHistoryUseCase
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    single { UserRepository() }
    single { SettingsRepository(get()) }
    single { HistoryRepository(get()) }

    factory { GetHistoryUseCase(get()) }
    factory { ManageHistoryUseCase(get()) }

    viewModel {
        WelcomeViewModel(
            userRepository = get(),
            cameraManager = get(),
            settingsRepository = get()
        )
    }
}
