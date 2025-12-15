package com.example.yangdnashabschlussprojekt.ui.component.welcome

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // Modifier als Best Practice

@Composable
fun WelcomeGreeting(
    userName: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hallo, $userName 👋",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}