package com.example.yangdnashabschlussprojekt.feature.repository

import com.russhwolf.settings.Settings

class SettingsOnboardingGateway(
    private val settings: Settings
) : OnboardingGateway {
    override fun restartOnboarding() {
        settings.putBoolean("onboarding_complete", false)
    }
}
