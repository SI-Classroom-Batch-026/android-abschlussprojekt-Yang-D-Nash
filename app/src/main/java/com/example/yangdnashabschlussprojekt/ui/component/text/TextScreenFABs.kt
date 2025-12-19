package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onCloudScanTriggered: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    val activeContentColor = Color.White

    Column(
        modifier = modifier.padding(FAB_SPACING),
        verticalArrangement = Arrangement.spacedBy(FAB_SPACING),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedFab(isVisible = visible, delay = 0) {
            HoldToScanButton(
                onTrigger = onCloudScanTriggered
            )
        }
        AnimatedFab(isVisible = visible, delay = 100) {
            val containerColor by animateColorAsState(
                if (isRestartButtonEnabled) MaterialTheme.colorScheme.error else disabledContainerColor,
                label = "restartColor"
            )
            FloatingActionButton(
                onClick = { if (isRestartButtonEnabled) onRestartClick() },
                containerColor = containerColor,
                elevation = FloatingActionButtonDefaults.elevation(if (isRestartButtonEnabled) 6.dp else 0.dp)
            ) {
                FabContent(
                    icon = Icons.Filled.RestartAlt,
                    label = "Restart",
                    tint = if (isRestartButtonEnabled) activeContentColor else disabledContentColor
                )
            }
        }

        AnimatedFab(isVisible = visible, delay = 200) {
            val containerColor by animateColorAsState(
                if (isSaveButtonEnabled) MaterialTheme.colorScheme.primary else disabledContainerColor,
                label = "saveColor"
            )
            FloatingActionButton(
                onClick = { if (isSaveButtonEnabled) onSaveClick() },
                containerColor = containerColor,
                elevation = FloatingActionButtonDefaults.elevation(if (isSaveButtonEnabled) 6.dp else 0.dp)
            ) {
                FabContent(
                    icon = Icons.Default.Save,
                    label = "Save",
                    tint = if (isSaveButtonEnabled) activeContentColor else disabledContentColor
                )
            }
        }

        AnimatedFab(isVisible = visible, delay = 300) {
            FloatingActionButton(
                onClick = onHistoryClick,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                FabContent(
                    icon = Icons.AutoMirrored.Filled.List,
                    label = "Verlauf",
                    tint = activeContentColor
                )
            }
        }
    }
}


