package com.example.yangdnashabschlussprojekt.feature.repository

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsOnboardingGateway(
    private val settings: Settings
) : OnboardingGateway {
    private val onboardingState = MutableStateFlow(
        settings.getBoolean("onboarding_complete", false)
    )

    override val isOnboardingComplete: Flow<Boolean> = onboardingState.asStateFlow()

    override fun completeOnboarding() {
        settings.putBoolean("onboarding_complete", true)
        onboardingState.value = true
    }

    override fun restartOnboarding() {
        settings.putBoolean("onboarding_complete", false)
        onboardingState.value = false
    }
}
