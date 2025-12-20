package com.example.yangdnashabschlussprojekt.data.local.repository

import android.content.Context
import androidx.core.content.edit

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    fun isOnboardingComplete(): Boolean {
        return prefs.getBoolean("onboarding_complete", false)
    }
    fun setOnboardingComplete(complete: Boolean) {
        prefs.edit { putBoolean("onboarding_complete", complete) }
    }
}