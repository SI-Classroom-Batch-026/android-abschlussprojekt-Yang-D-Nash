package com.example.yangdnashabschlussprojekt.ui.component.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(
    onBack: () -> Unit,
    onClearAll: () -> Unit,
    isClearAllEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White
        ),
        title = { Text("Verlauf", fontWeight = FontWeight.Bold) },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = Color.White
                )
            }
        },
        actions = {
            if (isClearAllEnabled) {
                TextButton(onClick = onClearAll) {
                    Text("Alles löschen", color = Color.Red.copy(alpha = 0.8f))
                }
            }
        }
    )
}