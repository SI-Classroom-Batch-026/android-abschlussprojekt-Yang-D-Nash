package com.example.yangdnashabschlussprojekt.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.user.LoginForm
import com.example.yangdnashabschlussprojekt.ui.component.user.ProfileImage
import com.example.yangdnashabschlussprojekt.ui.component.user.RegistrationForm
import com.example.yangdnashabschlussprojekt.ui.component.user.UserInfo
import com.example.yangdnashabschlussprojekt.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val userName by viewModel.userName.collectAsState(initial = "Gast")
    val registrationResult by viewModel.registrationResult.collectAsState()
    val authResult by viewModel.authResult.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(profileImageUri) { profileImageUri = it }
        Spacer(modifier = Modifier.height(16.dp))
        UserInfo(userName)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.logout() }) { Text("Ausloggen") }
        Spacer(modifier = Modifier.height(32.dp))

        RegistrationForm(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            displayName = displayName,
            onDisplayNameChange = { displayName = it },
            onRegisterClick = { viewModel.registerUser(email.trim(), password.trim(), displayName.trim(), profileImageUri) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginForm(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            onLoginClick = { viewModel.login(email.trim(), password.trim()) }
        )

        registrationResult.second?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        authResult?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
    }
}

