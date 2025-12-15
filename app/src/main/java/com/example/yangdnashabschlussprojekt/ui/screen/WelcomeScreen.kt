package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.welcome.SettingsButton
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeGreeting
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeImage
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = koinViewModel(),
    onOpenSettings: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)

    val displayName = currentUser?.displayName ?: "Gast"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WelcomeImage()
        Spacer(modifier = Modifier.height(24.dp))

        WelcomeGreeting(displayName)

        Spacer(modifier = Modifier.height(32.dp))

        if (currentUser == null) {
            SettingsButton(onClick = onOpenSettings)
        }
    }
}