package com.example.yangdnashabschlussprojekt.ui.component.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionRow(title: String, enabled: Boolean, onClick: () -> Unit) {
    val statusText = if (enabled) "Erlaubt" else "Blockiert"
    val statusColor = if (enabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)

            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = statusColor
            )
        }

        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Einstellungen für $title öffnen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant // Gedämpfte Farbe
            )
        }
    }
}