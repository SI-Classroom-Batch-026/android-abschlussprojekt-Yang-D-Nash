package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    val historyItems by viewModel.historyState.collectAsState()
    var selectedItemForDetail by remember { mutableStateOf<HistoryItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF001214), Color.Black)))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                HistoryTopBar(
                    onBack = onBack,
                    onClearAll = viewModel::clearAllHistory,
                    isClearAllEnabled = historyItems.isNotEmpty()
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (historyItems.isEmpty()) {
                    EmptyHistoryMessage()
                } else {
                    HistoryList(
                        historyItems = historyItems,
                        onDelete = { item -> viewModel.deleteHistoryItem(item) },
                        onSelect = { item -> selectedItemForDetail = item }
                    )
                }
            }
        }

        // --- Futuristische Detailansicht ---
        selectedItemForDetail?.let { item ->
            Dialog(onDismissRequest = { selectedItemForDetail = null }) {
                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = Color(0xFF1A1A1A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Scan Details",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            if (item.isFromCloud) {
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Default.Cloud, contentDescription = null, tint = Color.Cyan, modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(text = item.timestampFormatted, style = MaterialTheme.typography.labelMedium, color = Color.Gray)

                        Spacer(Modifier.height(24.dp))

                        Text(text = "ORIGINAL", style = MaterialTheme.typography.labelSmall, color = Color.Cyan, fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = item.recognizedText,
                            color = Color.White,
                            modifier = Modifier
                                .heightIn(max = 120.dp)
                                .verticalScroll(rememberScrollState())
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(text = "ÜBERSETZUNG", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF00FF), fontWeight = FontWeight.ExtraBold)
                        Text(
                            text = item.translatedText,
                            color = Color.White,
                            modifier = Modifier
                                .heightIn(max = 120.dp)
                                .verticalScroll(rememberScrollState())
                        )

                        Spacer(Modifier.height(32.dp))

                        Button(
                            onClick = {
                                onHistoryItemSelected(item)
                                selectedItemForDetail = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Im Scanner öffnen", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}