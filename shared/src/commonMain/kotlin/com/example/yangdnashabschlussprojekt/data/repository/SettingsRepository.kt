package com.example.yangdnashabschlussprojekt.data.repository// src/commonMain/kotlin/.../SettingsRepository.kt

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class SettingsRepository(private val settings: Settings) {

    fun saveUserName(name: String) {
        settings["user_name"] = name
    }

    fun getUserName(): String? {
        return settings.getStringOrNull("user_name")
    }

    fun clear() {
        settings.clear()
    }
}