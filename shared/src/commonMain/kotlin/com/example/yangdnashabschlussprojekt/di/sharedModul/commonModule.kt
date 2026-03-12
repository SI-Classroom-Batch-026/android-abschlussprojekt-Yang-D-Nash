package com.example.yangdnashabschlussprojekt.di.sharedModul

import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedHistoryViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedCaptureViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedRegistrationViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedSettingsViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedWelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.dsl.module

val commonModule = module {
    viewModel {
        WelcomeViewModel(
            cameraManager = get()
        )
    }
    viewModel {
        SharedWelcomeViewModel(
            sessionGateway = get(),
            onboardingGateway = get()
        )
    }
    viewModel {
        SharedSettingsViewModel(
            sessionGateway = get()
        )
    }
    viewModel {
        SharedRegistrationViewModel(
            sessionGateway = get()
        )
    }
    viewModel {
        SharedHistoryViewModel(
            historyGateway = get()
        )
    }
    viewModel {
        SharedCaptureViewModel(
            cameraManager = get(),
            captureGateway = get(),
            cloudVisionRepository = get()
        )
    }
}
