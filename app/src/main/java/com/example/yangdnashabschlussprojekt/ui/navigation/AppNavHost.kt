package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yangdnashabschlussprojekt.ui.screen.ARScreen
import com.example.yangdnashabschlussprojekt.ui.screen.SettingsScreen
import com.example.yangdnashabschlussprojekt.ui.screen.TextScreen
import com.example.yangdnashabschlussprojekt.ui.screen.WelcomeScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = WelcomeRoute.key
    ) {

        composable<WelcomeRoute> {
            WelcomeScreen(
                viewModel = koinViewModel(),
                onOpenSettings = { navController.navigate(SettingsRoute.key) }
            )
        }

        composable<SettingsRoute> {
            SettingsScreen(viewModel = koinViewModel())
        }

        composable<ARScreenRoute> { ARScreen(
            viewModel = koinViewModel(),
            onBack = TODO()
        ) }
        composable<TextScreenRoute> { TextScreen(
            viewModel = koinViewModel(),
            onBack = TODO()
        ) }
    }
}
