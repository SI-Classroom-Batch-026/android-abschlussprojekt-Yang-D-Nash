package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.ui.screen.*
import com.example.yangdnashabschlussprojekt.ui.viewmodel.* // WICHTIG: Deine ViewModels importieren
import com.example.yangdnashabschlussprojekt.ui.viewmodel.shared.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    navController: NavHostController,
    settingsRepository: SettingsRepository = koinInject()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val startDest: Any = remember {
        if (settingsRepository.isOnboardingComplete()) WelcomeRoute else OnboardingRoute
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (currentDestination?.hasRoute<OnboardingRoute>() == false) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.fillMaxSize().padding(top = innerPadding.calculateTopPadding())
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen(
                    viewModel = koinViewModel<AndroidWelcomeViewModel>(),
                    onOpenSettings = { navController.navigate(SettingsRoute) },
                    onNavigateToOnboarding = { navController.navigate(OnboardingRoute) }
                )
            }

            composable<OnboardingRoute> {
                OnboardingScreen {
                    settingsRepository.setOnboardingComplete(true)
                    navController.navigate(WelcomeRoute) { popUpTo<OnboardingRoute> { inclusive = true } }
                }
            }

            composable<ARScreenRoute> { ARScreen() }

            composable<SettingsRoute> {
                SettingsScreen(
                    settingsViewModel = koinViewModel<SettingsViewModel>(),
                    onNavigateToRegister = { navController.navigate(RegisterRoute) },
                    onNavigateToHistory = { navController.navigate(HistoryRoute) }
                )
            }

            composable<TextScreenRoute> {
                TextScreen(onNavigateToHistory = { navController.navigate(HistoryRoute) })
            }

            composable<RegisterRoute> {
                RegistrationScreen(
                    viewModel = koinViewModel<SettingsViewModel>(),
                    onBack = { navController.popBackStack() }
                )
            }

            composable<HistoryRoute> {
                HistoryScreen(
                    viewModel = koinViewModel<HistoryViewModel>(),
                    onBack = { navController.popBackStack() },
                    onHistoryItemSelected = { }
                )
            }
        }
    }
}