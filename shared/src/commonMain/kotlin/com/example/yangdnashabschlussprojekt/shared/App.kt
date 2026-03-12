package com.example.yangdnashabschlussprojekt.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.feature.model.SettingsPermissionSnapshot
import com.example.yangdnashabschlussprojekt.feature.ui.SharedHistoryScreen
import com.example.yangdnashabschlussprojekt.feature.ui.SharedCaptureRoute
import com.example.yangdnashabschlussprojekt.feature.ui.SharedRegistrationRoute
import com.example.yangdnashabschlussprojekt.feature.ui.SharedSettingsScreen
import com.example.yangdnashabschlussprojekt.feature.ui.SharedWelcomeScreen
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedHistoryViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedSettingsViewModel
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedWelcomeViewModel
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import org.koin.compose.viewmodel.koinViewModel

private enum class SharedDestination {
    Home,
    Capture,
    Welcome,
    Settings,
    Registration,
    History
}

@Composable
fun App() {
    MaterialTheme {
        val homeViewModel: WelcomeViewModel = koinViewModel()
        val welcomeViewModel: SharedWelcomeViewModel = koinViewModel()
        val settingsViewModel: SharedSettingsViewModel = koinViewModel()
        val historyViewModel: SharedHistoryViewModel = koinViewModel()

        val homeText by homeViewModel.uiState.collectAsState()
        val displayName by welcomeViewModel.displayName.collectAsState()
        val currentUser by settingsViewModel.currentUser.collectAsState()
        val authResult by settingsViewModel.authResult.collectAsState()
        val historyItems by historyViewModel.historyState.collectAsState()

        var destination by remember { mutableStateOf(SharedDestination.Home) }

        when (destination) {
            SharedDestination.Home -> SharedHomeScreen(
                statusText = homeText,
                onCameraTest = homeViewModel::onCameraButtonClick,
                onSharedUiTest = {
                    homeViewModel.updateText("Die gemeinsame KMP-Shell steuert jetzt Welcome, Settings und History.")
                },
                onOpenCapture = { destination = SharedDestination.Capture },
                onOpenWelcome = { destination = SharedDestination.Welcome },
                onOpenSettings = { destination = SharedDestination.Settings },
                onOpenHistory = { destination = SharedDestination.History }
            )
            SharedDestination.Capture -> SharedCaptureRoute(
                onBack = { destination = SharedDestination.Home },
                onOpenHistory = { destination = SharedDestination.History }
            )
            SharedDestination.Welcome -> SharedWelcomeScreen(
                displayName = displayName,
                onRestartOnboarding = {
                    welcomeViewModel.restartOnboarding()
                    settingsViewModel.showMessage("Onboarding-Status wurde zurueckgesetzt.")
                    destination = SharedDestination.Home
                }
            )
            SharedDestination.Settings -> SharedSettingsScreen(
                currentUser = currentUser,
                authMessage = authResult,
                permissions = SettingsPermissionSnapshot(
                    notificationsEnabled = true,
                    cameraGranted = true,
                    locationGranted = false,
                    microphoneGranted = false
                ),
                onLogin = settingsViewModel::login,
                onLogout = settingsViewModel::logout,
                onOpenHistory = { destination = SharedDestination.History },
                onOpenRegister = { destination = SharedDestination.Registration },
                onOpenSystemSettings = {
                    settingsViewModel.showMessage("Systemeinstellungen werden je Plattform als naechster Schritt verdrahtet.")
                },
                onBack = { destination = SharedDestination.Home },
                showRegisterAction = true
            )
            SharedDestination.Registration -> SharedRegistrationRoute(
                onBack = { destination = SharedDestination.Settings }
            )
            SharedDestination.History -> SharedHistoryScreen(
                historyItems = historyItems,
                onDelete = historyViewModel::deleteHistoryItem,
                onClearAll = historyViewModel::clearAllHistory,
                onOpenItem = {
                    homeViewModel.updateText("Verlaufsdetail gewaehlt: ${it.recognizedText.take(30)}")
                },
                onBack = { destination = SharedDestination.Home }
            )
        }
    }
}

@Composable
private fun SharedHomeScreen(
    statusText: String,
    onCameraTest: () -> Unit,
    onSharedUiTest: () -> Unit,
    onOpenCapture: () -> Unit,
    onOpenWelcome: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHistory: () -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF082228), Color.Black)))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "SmartVision KMP",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = statusText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.74f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCameraTest,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("Kamera testen")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onSharedUiTest,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("KMP-Shell pruefen")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onOpenCapture,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("Capture oeffnen")
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onOpenWelcome,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("Welcome oeffnen")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("Settings oeffnen")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onOpenHistory,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text("History oeffnen")
                }
            }
        }
    }
}
