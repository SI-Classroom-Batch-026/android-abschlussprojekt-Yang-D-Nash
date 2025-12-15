package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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

private val FAB_SPACING = 16.dp

@Composable
fun TextScreenFABs(
    onSaveClick: () -> Unit,
    isSaveButtonEnabled: Boolean,
    onHistoryClick: () -> Unit,
    onRestartClick: () -> Unit,
    isRestartButtonEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val activeContentColor = Color.White

    val saveContainerColor = if (isSaveButtonEnabled) MaterialTheme.colorScheme.primary else disabledContainerColor
    val restartContainerColor = if (isRestartButtonEnabled) MaterialTheme.colorScheme.error else disabledContainerColor

    Column(
        modifier = modifier.padding(FAB_SPACING),
        verticalArrangement = Arrangement.spacedBy(FAB_SPACING)
    ) {

        FloatingActionButton(
            onClick = {
                if (isRestartButtonEnabled) {
                    onRestartClick()
                }
            },
            containerColor = restartContainerColor,
            elevation = if (isRestartButtonEnabled) FloatingActionButtonDefaults.elevation() else FloatingActionButtonDefaults.elevation(0.dp),
            content = {
                val tintColor = if (isRestartButtonEnabled) activeContentColor else disabledContentColor
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.RestartAlt,
                        contentDescription = "Analyse neu starten",
                        tint = tintColor
                    )
                    Text(
                        "Restart",
                        color = tintColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )

        FloatingActionButton(
            onClick = {
                if (isSaveButtonEnabled) {
                    onSaveClick()
                }
            },
            containerColor = saveContainerColor,
            elevation = if (isSaveButtonEnabled) FloatingActionButtonDefaults.elevation() else FloatingActionButtonDefaults.elevation(0.dp),
            content = {
                val tintColor = if (isSaveButtonEnabled) activeContentColor else disabledContentColor
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Save,
                        contentDescription = "Text speichern",
                        tint = tintColor
                    )
                    Text("Speichern",
                        color = tintColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )

        FloatingActionButton(
            onClick = onHistoryClick,
            containerColor = MaterialTheme.colorScheme.primary,
            content = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = "Verlauf anzeigen",
                        tint = activeContentColor
                    )
                    Text(
                        "Verlauf",
                        color = activeContentColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )
    }
}