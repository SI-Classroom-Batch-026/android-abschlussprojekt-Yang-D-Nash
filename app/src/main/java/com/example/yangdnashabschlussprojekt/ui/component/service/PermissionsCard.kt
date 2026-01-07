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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.R

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
            // Nutzt perm_card_title aus deiner strings.xml
            Text(
                text = stringResource(R.string.perm_card_title),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            PermissionRow(
                title = stringResource(R.string.perm_notifications),
                enabled = notificationsEnabled,
                onClick = onOpenSettings
            )
            PermissionRow(
                title = stringResource(R.string.perm_camera),
                enabled = cameraGranted,
                onClick = onOpenSettings
            )
            PermissionRow(
                title = stringResource(R.string.perm_location),
                enabled = locationGranted,
                onClick = onOpenSettings
            )
            PermissionRow(
                title = stringResource(R.string.perm_microphone),
                enabled = microphoneGranted,
                onClick = onOpenSettings
            )
        }
    }
}