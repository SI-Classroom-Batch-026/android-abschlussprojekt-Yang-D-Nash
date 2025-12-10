package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TextScreenFABs(
    onSaveClick: () -> Unit,
    isSaveButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
    onHistoryClick: () -> Unit
) {
    val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val activeColor = MaterialTheme.colorScheme.primary
    val saveButtonColor = if (isSaveButtonEnabled) activeColor else disabledColor


    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FloatingActionButton(
            onClick = onHistoryClick,
            containerColor = activeColor
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.AutoMirrored.Filled.List,
                    contentDescription = "Verlauf anzeigen",
                    tint = Color.White
                )
                Text(
                    "Verlauf",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        FloatingActionButton(
            onClick = {
                if (isSaveButtonEnabled) {
                    onSaveClick()
                }
            },
            containerColor = saveButtonColor
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = "Text speichern",
                    tint = if (isSaveButtonEnabled) Color.White else Color.Black.copy(alpha = 0.5f)
                )
                Text("Speichern",
                    color = if (isSaveButtonEnabled) Color.White else Color.Black.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}