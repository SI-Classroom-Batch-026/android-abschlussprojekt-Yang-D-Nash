package com.example.yangdnashabschlussprojekt.ui.component.history

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(onBack: () -> Unit, onClearAll: () -> Unit) {
    TopAppBar(
        title = { Text("Text-Verlauf") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
            }
        },
        actions = {
            TextButton(onClick = onClearAll) {
                Text("Alles Löschen", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}