package com.example.yangdnashabschlussprojekt.ui.component.service

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PermissionsCard(
    notificationsEnabled: Boolean,
    cameraGranted: Boolean,
    locationGranted: Boolean,
    microphoneGranted: Boolean,
    onOpenSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(24.dp)) {
            Text("Berechtigungen", style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            PermissionRow("Benachrichtigungen", notificationsEnabled, onOpenSettings)
            PermissionRow("Kamera", cameraGranted, onOpenSettings)
            PermissionRow("Standort", locationGranted, onOpenSettings)
            PermissionRow("Mikrofon", microphoneGranted, onOpenSettings)
        }
    }
}

