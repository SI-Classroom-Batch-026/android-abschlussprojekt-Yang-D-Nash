package com.example.yangdnashabschlussprojekt.ui.component.welcome

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun WelcomeGreeting(
    userName: String
) {
    Text(
        text = "Hallo, $userName 👋",
        style = MaterialTheme.typography.headlineMedium,
        color = Color(0xFF006064)
    )
}