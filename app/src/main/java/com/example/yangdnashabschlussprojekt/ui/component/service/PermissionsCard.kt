package com.example.yangdnashabschlussprojekt.ui.component.service

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PermissionsCard(
    notificationsEnabled: Boolean,
    cameraGranted: Boolean,
    locationGranted: Boolean,
    microphoneGranted: Boolean,
    onOpenSettings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Berechtigungen",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(16.dp))

            PermissionRow("Benachrichtigungen", notificationsEnabled, onOpenSettings)
            PermissionRow("Kamera", cameraGranted, onOpenSettings)
            PermissionRow("Standort", locationGranted, onOpenSettings)
            PermissionRow("Mikrofon", microphoneGranted, onOpenSettings)
        }
    }
}