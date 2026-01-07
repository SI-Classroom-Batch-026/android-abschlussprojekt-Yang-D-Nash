package com.example.yangdnashabschlussprojekt.ui.screen

import android.Manifest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.yangdnashabschlussprojekt.R
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
    onBack: () -> Unit,
    settingsViewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    androidx.activity.compose.BackHandler {
        onBack()
    }

    val currentUser by settingsViewModel.currentUser.collectAsState()
    val authResult by settingsViewModel.authResult.collectAsState()

    var notificationsEnabled by remember { mutableStateOf(false) }
    var cameraGranted by remember { mutableStateOf(false) }
    var locationGranted by remember { mutableStateOf(false) }
    var microphoneGranted by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    fun refreshSystemPermissions() {
        notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        cameraGranted = isPermissionGranted(context, Manifest.permission.CAMERA)
        locationGranted = isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
        microphoneGranted = isPermissionGranted(context, Manifest.permission.RECORD_AUDIO)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshSystemPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) { refreshSystemPermissions() }

    LaunchedEffect(authResult) {
        authResult?.let { message -> scope.launch { snackbarHostState.showSnackbar(message) } }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF001214), Color.Black)))) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { CustomSnackbarHost(hostState = snackbarHostState) },
            topBar = {
                androidx.compose.material3.TopAppBar(
                    title = { Text(stringResource(R.string.settings_title), color = Color.White) },
                    colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        androidx.compose.material3.IconButton(onClick = onBack) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.btn_back),
                                tint = Color.White
                            )
                        }
                    }
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
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        ProfileImage()
                        Spacer(Modifier.height(16.dp))
                        UserInfo(currentUser?.displayName ?: stringResource(R.string.welcome_guest))
                        if (currentUser != null) {
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = onNavigateToHistory,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) { Text(stringResource(R.string.history_title)) }
                            TextButton(onClick = { settingsViewModel.logout() }) {
                                Text(stringResource(R.string.btn_logout), color = Color.Red.copy(alpha = 0.7f))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                if (currentUser == null) {
                    LoginForm(
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        onLoginClick = { settingsViewModel.login(email.trim(), password.trim()) },
                        onRegisterClick = onNavigateToRegister
                    )
                }
                Spacer(Modifier.height(24.dp))
                PermissionsCard(
                    notificationsEnabled = notificationsEnabled,
                    cameraGranted = cameraGranted,
                    locationGranted = locationGranted,
                    microphoneGranted = microphoneGranted,
                    onOpenSettings = { openAppSettings(context) }
                )
                Spacer(Modifier.height(100.dp))
            }
        }
    }
}