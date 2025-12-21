package com.example.yangdnashabschlussprojekt.ui.component.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.HistoryItem

@Composable
fun HistoryList(
    historyItems: List<HistoryItem>, // Nutzt jetzt das neue Domain-Modell
    onDelete: (HistoryItem) -> Unit, // Übergibt das ganze Objekt statt nur der Long-ID
    onSelect: (HistoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = historyItems,
            // Wir nutzen die Cloud-ID oder die lokale ID als Key
            key = { it.firestoreId ?: it.id ?: it.rawTimestamp }
        ) { item ->
            HistoryCard(
                item = item,
                onDelete = { onDelete(item) }, // Reicht das Item an das ViewModel weiter
                onSelect = { onSelect(item) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}