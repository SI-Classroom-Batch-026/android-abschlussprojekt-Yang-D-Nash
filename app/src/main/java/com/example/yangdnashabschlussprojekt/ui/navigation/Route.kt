package com.example.yangdnashabschlussprojekt.ui.navigation

import kotlinx.serialization.Serializable

interface NavRoute {
    val route: String
    val label: String
}
@Serializable
object WelcomeRoute: NavRoute {
    override val route = "welcome"
    override val label = "Home"
}
@Serializable
object SettingsRoute : NavRoute{
    override val route = "settings"
    override val label = "Settings"
}
@Serializable
object ARScreenRoute: NavRoute {
    override val route = "arscreen"
    override val label = "AR"
}
@Serializable
object TextScreenRoute : NavRoute{
    override val route = "textscreen"
    override val label = "Text"
}
@Serializable
object RegisterRoute : NavRoute{
    override val route = "register"
    override val label = "Register"
}
@Serializable
object HistoryRoute : NavRoute{
    override val route = "history"
    override val label = "Verlauf"
}