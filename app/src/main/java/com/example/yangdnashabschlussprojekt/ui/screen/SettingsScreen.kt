package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.yangdnashabschlussprojekt.data.companion.DesktopCompanionClient
import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.feature.model.SettingsPermissionSnapshot
import com.example.yangdnashabschlussprojekt.feature.ui.SharedSettingsScreen
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedSettingsViewModel
import com.example.yangdnashabschlussprojekt.util.notification.isPermissionGranted
import com.example.yangdnashabschlussprojekt.util.notification.openAppSettings
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onBack: () -> Unit,
    settingsViewModel: SharedSettingsViewModel = koinViewModel(),
    settingsRepository: SettingsRepository = koinInject(),
    desktopCompanionClient: DesktopCompanionClient = koinInject()
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val currentUser by settingsViewModel.currentUser.collectAsState()
    val authResult by settingsViewModel.authResult.collectAsState()

    androidx.activity.compose.BackHandler(onBack = onBack)

    var notificationsEnabled by remember { mutableStateOf(false) }
    var cameraGranted by remember { mutableStateOf(false) }
    var locationGranted by remember { mutableStateOf(false) }
    var microphoneGranted by remember { mutableStateOf(false) }
    var desktopCompanionHost by remember { mutableStateOf(settingsRepository.getDesktopCompanionHost()) }
    var desktopCompanionMessage by remember { mutableStateOf<String?>(null) }

    fun refreshSystemPermissions() {
        notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        cameraGranted = isPermissionGranted(context, Manifest.permission.CAMERA)
        locationGranted = isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
        microphoneGranted = isPermissionGranted(context, Manifest.permission.RECORD_AUDIO)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshSystemPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        refreshSystemPermissions()
    }

    SharedSettingsScreen(
        currentUser = currentUser,
        authMessage = authResult,
        permissions = SettingsPermissionSnapshot(
            notificationsEnabled = notificationsEnabled,
            cameraGranted = cameraGranted,
            locationGranted = locationGranted,
            microphoneGranted = microphoneGranted
        ),
        onLogin = settingsViewModel::login,
        onLogout = settingsViewModel::logout,
        onOpenHistory = onNavigateToHistory,
        onOpenRegister = onNavigateToRegister,
        onOpenSystemSettings = { openAppSettings(context) },
        onBack = onBack,
        companionHost = desktopCompanionHost,
        companionStatusMessage = desktopCompanionMessage,
        onCompanionHostChange = { desktopCompanionHost = it },
        onConnectCompanion = {
            scope.launch {
                desktopCompanionMessage = desktopCompanionClient.connect(desktopCompanionHost)
                    .fold(
                        onSuccess = { message ->
                            desktopCompanionHost = settingsRepository.getDesktopCompanionHost()
                            desktopCompanionClient.publishStatus(
                                mode = com.example.yangdnashabschlussprojekt.feature.model.CompanionMode.SETTINGS,
                                statusMessage = "SmartVision auf ${Build.MODEL ?: "Android"} ist verbunden."
                            )
                            message
                        },
                        onFailure = { error ->
                            error.message ?: "Desktop Companion konnte nicht erreicht werden."
                        }
                    )
            }
        }
    )
}
