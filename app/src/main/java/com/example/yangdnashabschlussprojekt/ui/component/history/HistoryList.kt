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

@Composable
fun HistoryList(
    historyItems: List<HistoryItem>,
    onDelete: (Long) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
    ) {
        items(
            items = historyItems,
            key = { it.id }
        ) { item ->
            HistoryCard(item = item, onDelete = { onDelete(item.id) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
