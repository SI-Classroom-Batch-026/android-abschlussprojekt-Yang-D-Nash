package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = WelcomeRoute.route
        ) {
            composable(WelcomeRoute.route) {
                WelcomeScreen(
                    viewModel = koinViewModel(),
                    onOpenSettings = { navController.navigate(SettingsRoute.route) }
                )
            }
            composable(ARScreenRoute.route) {
                ARScreen(
                    viewModel = koinViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(SettingsRoute.route){
                SettingsScreen(
                    viewModel = koinViewModel(),
                )
            }
            composable(TextScreenRoute.route) {
                TextScreen(
                    viewModel = koinViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}