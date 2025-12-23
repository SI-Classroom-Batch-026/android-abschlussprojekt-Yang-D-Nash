package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.yangdnashabschlussprojekt.ui.component.camera.HoldToScanButton

private val FAB_SPACING = 16.dp

@Composable
fun TextScreenFABs(
    isLiveActive: Boolean,
    onLiveToggle: () -> Unit,
    onSaveClick: () -> Unit,
    isSaveButtonEnabled: Boolean,
    onHistoryClick: () -> Unit,
    onCloudScanTriggered: () -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    Column(
        modifier = modifier.padding(FAB_SPACING),
        verticalArrangement = Arrangement.spacedBy(FAB_SPACING),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedFab(isVisible = visible, delay = 0) {
            HoldToScanButton(onTrigger = onCloudScanTriggered)
        }
        AnimatedFab(isVisible = visible, delay = 100) {
            val containerColor by animateColorAsState(
                if (isLiveActive) MaterialTheme.colorScheme.primary else Color(0xFF424242),
                label = "liveColor"
            )
            FloatingActionButton(
                onClick = onLiveToggle,
                containerColor = containerColor,
            ) {
                FabContent(
                    icon = if (isLiveActive) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    label = if (isLiveActive) "Live Scan" else "Fixiert",
                    tint = Color.White
                )
            }
        }
        AnimatedFab(isVisible = visible, delay = 200) {
            val containerColor by animateColorAsState(
                if (isSaveButtonEnabled) MaterialTheme.colorScheme.secondary else disabledContainerColor,
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
                    tint = if (isSaveButtonEnabled) Color.White else disabledContentColor
                )
            }
        }
        AnimatedFab(isVisible = visible, delay = 300) {
            FloatingActionButton(
                onClick = onHistoryClick,
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                FabContent(
                    icon = Icons.AutoMirrored.Filled.List,
                    label = "Verlauf",
                    tint = Color.White
                )
            }
        }
    }
}
