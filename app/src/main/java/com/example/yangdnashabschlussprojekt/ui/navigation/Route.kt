package com.example.yangdnashabschlussprojekt.ui.navigation

sealed class Route<T : Route<T>> {
    abstract val key: String
}

object WelcomeRoute : Route<WelcomeRoute>() {
    override val key = "welcome"
}

object SettingsRoute : Route<SettingsRoute>() {
    override val key = "settings"
}

object ARScreenRoute : Route<ARScreenRoute>() {
    override val key = "arscreen"
}

object TextScreenRoute : Route<TextScreenRoute>() {
    override val key = "textscreen"
}
