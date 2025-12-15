package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yangdnashabschlussprojekt.ui.screen.ARScreen
import com.example.yangdnashabschlussprojekt.ui.screen.HistoryScreen
import com.example.yangdnashabschlussprojekt.ui.screen.RegistrationScreen
import com.example.yangdnashabschlussprojekt.ui.screen.SettingsScreen
import com.example.yangdnashabschlussprojekt.ui.screen.TextScreen
import com.example.yangdnashabschlussprojekt.ui.screen.WelcomeScreen
import com.example.yangdnashabschlussprojekt.ui.viewmodel.TextViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
) {

    var topBarTitle by remember { mutableStateOf("") } // Unbenutzt, aber belassen

    Scaffold(
        topBar = {
            if (topBarTitle.isNotEmpty()) {
                TopAppBar(title = { Text(topBarTitle) })
            }
        },
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
                    textViewModel = koinViewModel()
                )
            }


            composable(SettingsRoute.route) {
                SettingsScreen(
                    settingsViewModel = koinViewModel(),
                    onNavigateToRegister = { navController.navigate(RegisterRoute.route) },
                    onNavigateToHistory = { navController.navigate(HistoryRoute.route) }
                )
            }

            composable(TextScreenRoute.route) {
                TextScreen(
                    textViewModel = koinViewModel(),
                    arViewModel = koinViewModel(),
                    onNavigateToHistory = { navController.navigate(HistoryRoute.route) }
                )
            }

            composable(RegisterRoute.route) {
                RegistrationScreen(
                    viewModel = koinViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }

            composable(HistoryRoute.route) {
                val textViewModel: TextViewModel = koinViewModel()

                HistoryScreen(
                    viewModel = koinViewModel(),
                    onBack = { navController.popBackStack() },
                    onHistoryItemSelected = { item ->

                        textViewModel.loadFromHistory(
                            recognized = item.recognizedText,
                            translated = item.translatedText
                        )
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}