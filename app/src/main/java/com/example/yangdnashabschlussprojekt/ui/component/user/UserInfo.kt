package com.example.yangdnashabschlussprojekt.ui.component.user

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun UserInfo(userName: String) {
    Column {
        Text("Angemeldet als:", style = MaterialTheme.typography.titleMedium)
        Text(userName, style = MaterialTheme.typography.bodyLarge)
    }
}
