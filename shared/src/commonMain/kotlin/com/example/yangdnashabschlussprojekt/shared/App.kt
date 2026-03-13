package com.example.yangdnashabschlussprojekt.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import com.example.yangdnashabschlussprojekt.feature.model.SettingsPermissionSnapshot
import com.example.yangdnashabschlussprojekt.feature.ui.SharedHistoryScreen
import com.example.yangdnashabschlussprojekt.feature.ui.SharedOnboardingScreen
import com.example.yangdnashabschlussprojekt.feature.ui.PlatformArRoute
import com.example.yangdnashabschlussprojekt.feature.ui.PlatformTextRoute
import com.example.yangdnashabschlussprojekt.feature.ui.SharedRegistrationRoute
import com.example.yangdnashabschlussprojekt.feature.ui.SharedSettingsScreen
import com.example.yangdnashabschlussprojekt.feature.ui.SharedWelcomeScreen
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedHistoryViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedSettingsViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedWelcomeViewModel
import com.example.yangdnashabschlussprojekt.feature.repository.OnboardingGateway
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

private enum class SharedRoute {
    Onboarding,
    Welcome,
    Settings,
    Ar,
    Text,
    Registration,
    History
}

private enum class SharedTopLevelDestination(
    val route: SharedRoute,
    val label: String,
    val icon: @Composable () -> Unit
) {
    Welcome(
        route = SharedRoute.Welcome,
        label = "Smar",
        icon = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Welcome"
            )
        }
    ),
    Settings(
        route = SharedRoute.Settings,
        label = "Settings",
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings"
            )
        }
    ),
    Ar(
        route = SharedRoute.Ar,
        label = "AR",
        icon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "AR"
            )
        }
    ),
    Text(
        route = SharedRoute.Text,
        label = "Text",
        icon = {
            Icon(
                imageVector = Icons.Default.AccessibilityNew,
                contentDescription = "Text"
            )
        }
    )
}

@Composable
fun App() {
    val onboardingGateway: OnboardingGateway = koinInject()
    val cameraManager: CameraManager = koinInject()
    val welcomeViewModel: SharedWelcomeViewModel = koinViewModel()
    val settingsViewModel: SharedSettingsViewModel = koinViewModel()
    val historyViewModel: SharedHistoryViewModel = koinViewModel()

    val onboardingComplete by onboardingGateway.isOnboardingComplete.collectAsState(initial = false)
    val displayName by welcomeViewModel.displayName.collectAsState()
    val currentUser by settingsViewModel.currentUser.collectAsState()
    val authResult by settingsViewModel.authResult.collectAsState()
    val historyItems by historyViewModel.historyState.collectAsState()

    var route by remember { mutableStateOf<SharedRoute?>(null) }
    var historyReturnRoute by remember { mutableStateOf(SharedRoute.Text) }
    var arStatusMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(onboardingComplete) {
        route = when {
            !onboardingComplete -> SharedRoute.Onboarding
            route == null || route == SharedRoute.Onboarding -> SharedRoute.Welcome
            else -> route
        }
    }

    val currentRoute = route ?: if (onboardingComplete) SharedRoute.Welcome else SharedRoute.Onboarding

    SmartVisionSharedTheme {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (currentRoute != SharedRoute.Onboarding && currentRoute != SharedRoute.Registration) {
                    SharedBottomNavigationBar(
                        currentRoute = currentRoute,
                        onSelect = { selectedRoute ->
                            route = selectedRoute
                        }
                    )
                }
            }
        ) { innerPadding ->
            when (currentRoute) {
                SharedRoute.Onboarding -> SharedOnboardingScreen(
                    onFinished = {
                        onboardingGateway.completeOnboarding()
                        route = SharedRoute.Welcome
                    },
                    modifier = Modifier.fillMaxSize()
                )

                SharedRoute.Welcome -> SharedWelcomeScreen(
                    displayName = displayName,
                    onRestartOnboarding = {
                        welcomeViewModel.restartOnboarding()
                    },
                    contentPadding = PaddingValues(bottom = innerPadding.calculateBottomPadding())
                )

                SharedRoute.Settings -> SharedSettingsScreen(
                    currentUser = currentUser,
                    authMessage = authResult,
                    permissions = SettingsPermissionSnapshot(
                        notificationsEnabled = false,
                        cameraGranted = false,
                        locationGranted = false,
                        microphoneGranted = false
                    ),
                    onLogin = settingsViewModel::login,
                    onLogout = settingsViewModel::logout,
                    onOpenHistory = {
                        historyReturnRoute = SharedRoute.Settings
                        route = SharedRoute.History
                    },
                    onOpenRegister = { route = SharedRoute.Registration },
                    onOpenSystemSettings = {
                        settingsViewModel.showMessage(
                            "Systemeinstellungen werden auf ${cameraManager.platformName} plattformspezifisch angebunden."
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                )

                SharedRoute.Ar -> PlatformArRoute(
                    platformName = cameraManager.platformName,
                    statusMessage = arStatusMessage,
                    onOpenCamera = {
                        arStatusMessage = cameraManager.openCamera()
                    },
                    onOpenTextMode = { route = SharedRoute.Text },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                )

                SharedRoute.Text -> PlatformTextRoute(
                    platformName = cameraManager.platformName,
                    onOpenHistory = {
                        historyReturnRoute = SharedRoute.Text
                        route = SharedRoute.History
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                )

                SharedRoute.Registration -> SharedRegistrationRoute(
                    onBack = { route = SharedRoute.Settings }
                )

                SharedRoute.History -> SharedHistoryScreen(
                    historyItems = historyItems,
                    onDelete = historyViewModel::deleteHistoryItem,
                    onClearAll = historyViewModel::clearAllHistory,
                    onOpenItem = { },
                    onBack = { route = historyReturnRoute },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
        }
    }
}

@Composable
private fun SharedBottomNavigationBar(
    currentRoute: SharedRoute,
    onSelect: (SharedRoute) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .navigationBarsPadding()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(28.dp)),
        color = Color(0xFF1C1B1F).copy(alpha = 0.85f),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.height(64.dp),
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            SharedTopLevelDestination.entries.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onSelect(item.route) },
                    icon = item.icon,
                    label = {
                        Text(
                            text = item.label,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color.White.copy(alpha = 0.1f),
                        unselectedIconColor = Color.White.copy(alpha = 0.55f),
                        unselectedTextColor = Color.White.copy(alpha = 0.55f)
                    )
                )
            }
        }
    }
}
