package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.ui.screen.ARScreen
import com.example.yangdnashabschlussprojekt.ui.screen.HistoryScreen
import com.example.yangdnashabschlussprojekt.ui.screen.OnboardingScreen
import com.example.yangdnashabschlussprojekt.ui.screen.RegistrationScreen
import com.example.yangdnashabschlussprojekt.ui.screen.SettingsScreen
import com.example.yangdnashabschlussprojekt.ui.screen.TextScreen
import com.example.yangdnashabschlussprojekt.ui.screen.WelcomeScreen
import com.example.yangdnashabschlussprojekt.ui.viewmodel.AndroidWelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.HistoryViewModel
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
            val showBottomBar = currentDestination?.let { dest ->
                !dest.hasRoute<OnboardingRoute>() && !dest.hasRoute<RegisterRoute>()
            } ?: true

            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            composable<WelcomeRoute> {
                WelcomeScreen(
                    viewModel = koinViewModel<AndroidWelcomeViewModel>(),
                    onNavigateToOnboarding = { navController.navigate(OnboardingRoute) }
                )
            }
            composable<OnboardingRoute> {
                OnboardingScreen {
                    settingsRepository.setOnboardingComplete(true)
                    navController.navigate(WelcomeRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            }
            composable<SettingsRoute> {
                SettingsScreen(
                    settingsViewModel = koinViewModel<SettingsViewModel>(),
                    onNavigateToRegister = { navController.navigate(RegisterRoute) },
                    onNavigateToHistory = { navController.navigate(HistoryRoute) },
                    onBack = { navController.popBackStack() }
                )
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
                    onHistoryItemSelected = {  }
                )
            }
            composable<ARScreenRoute> { ARScreen() }
            composable<TextScreenRoute> {
                TextScreen(onNavigateToHistory = { navController.navigate(HistoryRoute) })
            }
        }
    }
}