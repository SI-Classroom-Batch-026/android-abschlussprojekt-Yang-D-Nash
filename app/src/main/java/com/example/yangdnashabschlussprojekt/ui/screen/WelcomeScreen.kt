package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.SettingsButton
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeGreeting
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeImage
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = koinViewModel(),
    onOpenSettings: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { androidx.compose.material3.Text("Willkommen") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WelcomeImage()
            Spacer(modifier = Modifier.height(24.dp))
            WelcomeGreeting(userName)
            Spacer(modifier = Modifier.height(32.dp))
            SettingsButton(onClick = onOpenSettings)
        }
    }
}
