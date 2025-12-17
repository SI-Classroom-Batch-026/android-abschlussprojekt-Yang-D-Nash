package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.yangdnashabschlussprojekt.ui.component.common.messaging.CustomSnackbarHost
import com.example.yangdnashabschlussprojekt.ui.component.service.PermissionsCard
import com.example.yangdnashabschlussprojekt.ui.component.user.ProfileImage
import com.example.yangdnashabschlussprojekt.ui.component.user.UserInfo
import com.example.yangdnashabschlussprojekt.ui.component.user.login.LoginForm
import com.example.yangdnashabschlussprojekt.ui.viewmodel.shared.SettingsViewModel
import com.example.yangdnashabschlussprojekt.util.notification.isPermissionGranted
import com.example.yangdnashabschlussprojekt.util.notification.openAppSettings
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToHistory: () -> Unit,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentUser by settingsViewModel.currentUser.collectAsState()
    val authResult by settingsViewModel.authResult.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(false) }
    var cameraGranted by remember { mutableStateOf(false) }
    var locationGranted by remember { mutableStateOf(false) }
    var microphoneGranted by remember { mutableStateOf(false) }
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
    LaunchedEffect(Unit) { refreshSystemPermissions() }
    LaunchedEffect(authResult) {
        authResult?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileImage()
            Spacer(Modifier.height(16.dp))
            UserInfo(currentUser?.displayName ?: "Gast")
            Spacer(Modifier.height(16.dp))
            if (currentUser != null) {
                Button(onClick = onNavigateToHistory) {
                    Text("Verlauf anzeigen")
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    settingsViewModel.logout()
                    scope.launch {
                        snackbarHostState.showSnackbar("Erfolgreich ausgeloggt.")
                    }
                }) {
                    Text("Ausloggen")
                }
            }
            else {
                Button(onClick = onNavigateToRegister) { Text("Registrieren") }
                Spacer(Modifier.height(16.dp))
                LoginForm(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    onLoginClick = {
                        settingsViewModel.login(email.trim(), password.trim())
                    }
                )
            }
            Spacer(Modifier.height(32.dp))
            PermissionsCard(
                notificationsEnabled = notificationsEnabled,
                cameraGranted = cameraGranted,
                locationGranted = locationGranted,
                microphoneGranted = microphoneGranted,
                onOpenSettings = { openAppSettings(context) }
            )
        }
    }
}