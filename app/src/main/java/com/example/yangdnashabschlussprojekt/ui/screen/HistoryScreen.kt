package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.HistoryItem
import com.example.yangdnashabschlussprojekt.ui.component.history.EmptyHistoryMessage
import com.example.yangdnashabschlussprojekt.ui.component.history.HistoryList
import com.example.yangdnashabschlussprojekt.ui.component.history.HistoryTopBar
import com.example.yangdnashabschlussprojekt.ui.viewmodel.HistoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = koinViewModel(),
    onBack: () -> Unit,
    onHistoryItemSelected: (HistoryItem) -> Unit
) {
    // Falls das ViewModel im androidMain ist, ist koinViewModel() richtig.
    // Falls es im commonMain ist, nutzt man meist viewModel() (Koin Compose).
    val historyItems by viewModel.historyState.collectAsState()

    var selectedItemForDetail by remember { mutableStateOf<HistoryItem?>(null) }

    Scaffold(
        topBar = {
            HistoryTopBar(
                onBack = onBack,
                onClearAll = viewModel::clearAllHistory,
                isClearAllEnabled = historyItems.isNotEmpty(),
                modifier = Modifier
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (historyItems.isEmpty()) {
                EmptyHistoryMessage()
            } else {
                HistoryList(
                    historyItems = historyItems,
                    // WICHTIG: Hier wird jetzt das ganze Item an das ViewModel gereicht
                    onDelete = { item -> viewModel.deleteHistoryItem(item) },
                    onSelect = { item -> selectedItemForDetail = item }
                )
            }
        }

        // --- Detail Dialog ---
        selectedItemForDetail?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItemForDetail = null },
                title = {
                    Row {
                        Text("Scan vom ${item.timestampFormatted}")
                        if (item.isFromCloud) { // Kleiner visueller Hinweis im Dialog
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.Cloud, contentDescription = null, tint = Color.Gray)
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Original:", style = MaterialTheme.typography.labelLarge, color = Color.Cyan)
                        Text(item.recognizedText, style = MaterialTheme.typography.bodyMedium)

                        Spacer(Modifier.height(16.dp))

                        Text("Übersetzung:", style = MaterialTheme.typography.labelLarge, color = Color.Magenta)
                        Text(item.translatedText, style = MaterialTheme.typography.bodyMedium)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onHistoryItemSelected(item)
                            selectedItemForDetail = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan)
                    ) {
                        Text("Im Scanner öffnen", color = Color.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedItemForDetail = null }) {
                        Text("Schließen")
                    }
                }
            )
        }
    }
}