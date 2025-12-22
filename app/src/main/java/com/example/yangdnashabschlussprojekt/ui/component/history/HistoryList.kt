// HistoryList.kt
package com.example.yangdnashabschlussprojekt.ui.component.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.HistoryItem

@Composable
fun HistoryList(
    historyItems: List<HistoryItem>,
    onDelete: (HistoryItem) -> Unit,
    onSelect: (HistoryItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = historyItems,
            key = { it.firestoreId ?: it.id ?: it.rawTimestamp }
        ) { item ->
            HistoryCard(
                item = item,
                onDelete = { onDelete(item) },
                onSelect = { onSelect(item) }
            )
        }
    }
}
