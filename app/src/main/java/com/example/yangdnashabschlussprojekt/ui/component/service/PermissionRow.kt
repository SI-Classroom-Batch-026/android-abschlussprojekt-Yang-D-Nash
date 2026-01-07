package com.example.yangdnashabschlussprojekt.ui.component.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.R

@Composable
fun PermissionRow(title: String, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = Color.White)
            Text(
                // Nutzt perm_status_enabled / perm_status_disabled
                text = if (enabled)
                    stringResource(R.string.perm_status_enabled)
                else
                    stringResource(R.string.perm_status_disabled),
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) Color(0xFF00FFD1) else Color(0xFFFF4B4B)
            )
        }
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.1f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings_title),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}