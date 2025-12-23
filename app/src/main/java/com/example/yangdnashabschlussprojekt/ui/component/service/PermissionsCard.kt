package com.example.yangdnashabschlussprojekt.ui.component.service

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

