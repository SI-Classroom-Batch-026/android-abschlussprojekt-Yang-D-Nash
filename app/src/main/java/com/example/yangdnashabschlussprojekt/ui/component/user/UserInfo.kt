package com.example.yangdnashabschlussprojekt.ui.component.user

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // Modifier als Best Practice

@Composable
fun UserInfo(userName: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            "Angemeldet als:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            userName,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}