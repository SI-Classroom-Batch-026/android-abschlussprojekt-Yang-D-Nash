package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
    onHistoryClick: () -> Unit,
    onRestartClick: () -> Unit,
    isRestartButtonEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val disabledColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val activeColor = MaterialTheme.colorScheme.primary
    val saveButtonColor = if (isSaveButtonEnabled) activeColor else disabledColor
    val restartColor = if (isRestartButtonEnabled) MaterialTheme.colorScheme.error else disabledColor


    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. Restart Button
        FloatingActionButton(
            onClick = {
                if (isRestartButtonEnabled) {
                    onRestartClick()
                }
            },
            containerColor = restartColor,
            elevation = if (isRestartButtonEnabled) FloatingActionButtonDefaults.elevation() else FloatingActionButtonDefaults.elevation(0.dp),
            // 💡 KORREKTUR: Explizite Zuweisung des 'content'-Blocks
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.RestartAlt,
                        contentDescription = "Analyse neu starten",
                        tint = if (isRestartButtonEnabled) Color.White else Color.Black.copy(alpha = 0.5f)
                    )
                    Text(
                        "Restart",
                        color = if (isRestartButtonEnabled) Color.White else Color.Black.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )

        Spacer(Modifier.height(8.dp)) // Kleiner Abstand

        // 2. Save Button
        FloatingActionButton(
            onClick = {
                if (isSaveButtonEnabled) {
                    onSaveClick()
                }
            },
            containerColor = saveButtonColor,
            // 💡 KORREKTUR: Explizite Zuweisung des 'content'-Blocks
            content = {
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
        )

        // 3. History Button
        FloatingActionButton(
            onClick = onHistoryClick,
            containerColor = activeColor,
            // 💡 KORREKTUR: Explizite Zuweisung des 'content'-Blocks
            content = {
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
        )
    }
}