package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.user.registration.RegistrationFeedback
import com.example.yangdnashabschlussprojekt.ui.component.user.registration.RegistrationForm
import com.example.yangdnashabschlussprojekt.ui.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegistrationScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val registrationResult by viewModel.registrationResult.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            email = currentUser!!.email ?: ""

            displayName = currentUser!!.displayName
        }
    }

    LaunchedEffect(registrationResult) {
        if (registrationResult?.success == true) {
            onBack()
            viewModel.resetRegistrationResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onBack) { Text("Zurück") }

        RegistrationForm(
            email = email,
            onEmailChange = { email = it },
            password = password,
            onPasswordChange = { password = it },
            displayName = displayName,
            onDisplayNameChange = { displayName = it },
            onRegisterClick = {
                viewModel.registerUser(email, password, displayName)
            }
        )
        RegistrationFeedback(result = registrationResult)
    }
}