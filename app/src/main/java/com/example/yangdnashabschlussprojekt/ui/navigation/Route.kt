package com.example.yangdnashabschlussprojekt.ui.navigation

import kotlinx.serialization.Serializable

sealed class Route<T : Route<T>> {
    abstract val key: String
}
@Serializable
object WelcomeRoute : Route<WelcomeRoute>() {
    override val key = "welcome"
}
@Serializable
object SettingsRoute : Route<SettingsRoute>() {
    override val key = "settings"
}
@Serializable
object ARScreenRoute : Route<ARScreenRoute>() {
    override val key = "arscreen"
}
@Serializable
object TextScreenRoute : Route<TextScreenRoute>() {
    override val key = "textscreen"
}
