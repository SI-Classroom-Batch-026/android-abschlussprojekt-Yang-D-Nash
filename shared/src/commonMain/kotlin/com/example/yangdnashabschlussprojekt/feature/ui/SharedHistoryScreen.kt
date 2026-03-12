package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.feature.model.SharedHistoryItem

@Composable
fun SharedHistoryScreen(
    historyItems: List<SharedHistoryItem>,
    onDelete: (SharedHistoryItem) -> Unit,
    onClearAll: () -> Unit,
    onOpenItem: (SharedHistoryItem) -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null
) {
    var selectedItem by remember { mutableStateOf<SharedHistoryItem?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF001214), Color.Black)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    TextButton(onClick = onBack) {
                        Text("Zurueck", color = Color.White)
                    }
                } else {
                    Spacer(modifier = Modifier.height(1.dp))
                }
                Text(
                    text = "Verlauf",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                if (historyItems.isNotEmpty()) {
                    TextButton(onClick = onClearAll) {
                        Text("Alles loeschen", color = Color(0xFFFF7A7A))
                    }
                } else {
                    Spacer(modifier = Modifier.height(1.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (historyItems.isEmpty()) {
                EmptyHistoryCard()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(historyItems, key = { it.stableKey }) { item ->
                        HistoryCard(
                            item = item,
                            onDelete = { onDelete(item) },
                            onSelect = { selectedItem = item }
                        )
                    }
                }
            }
        }

        selectedItem?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItem = null },
                confirmButton = {
                    Button(
                        onClick = {
                            onOpenItem(item)
                            selectedItem = null
                        }
                    ) {
                        Text("Eintrag oeffnen")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedItem = null }) {
                        Text("Schliessen")
                    }
                },
                title = {
                    Text(
                        text = "Eintragsdetails",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(item.timestampLabel, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Original", fontWeight = FontWeight.Bold)
                        Text(item.recognizedText)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Uebersetzung", fontWeight = FontWeight.Bold)
                        Text(item.translatedText.ifBlank { "Keine Uebersetzung gespeichert." })
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyHistoryCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Noch keine Eintraege",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Sobald OCR-, Uebersetzungs- oder Kameraaktionen gespeichert werden, tauchen sie hier plattformuebergreifend auf.",
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun HistoryCard(
    item: SharedHistoryItem,
    onDelete: () -> Unit,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.05f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.timestampLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.recognizedText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.translatedText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.translatedText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF7DEBFF),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            TextButton(onClick = onDelete) {
                Text("Loeschen", color = Color(0xFFFF7A7A))
            }
        }
    }
}
