package com.example.yangdnashabschlussprojekt.ui.component.common.messaging

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CustomSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(bottom = 80.dp)
    ) { data: SnackbarData ->
        Snackbar(
            snackbarData = data,
            shape = MaterialTheme.shapes.medium,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer, // Konsistente Hintergrundfarbe
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer, // Kontrastreiche Textfarbe
            actionColor = MaterialTheme.colorScheme.primary, // Farbe für die Action-Schaltfläche
        )
    }
}