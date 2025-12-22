package com.example.yangdnashabschlussprojekt.ui.component.user.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.remote.RegistrationResult

@Composable
fun RegistrationFeedback(result: RegistrationResult?, modifier: Modifier = Modifier) {
    result?.let {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val color = if (it.success) Color(0xFF00FFD1) else Color(0xFFFF4B4B)
            val icon = if (it.success) Icons.Default.CheckCircle else Icons.Default.Error

            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (it.success) "Erfolgreich registriert!" else it.errorMessage ?: "Fehler aufgetreten",
                color = color,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}