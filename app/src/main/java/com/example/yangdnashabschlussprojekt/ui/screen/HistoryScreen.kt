package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.yangdnashabschlussprojekt.ui.component.history.EmptyHistoryMessage
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.HistoryItem
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

        if (historyItems.isEmpty()) {
            EmptyHistoryMessage(Modifier.padding(paddingValues))
        } else {
            HistoryList(
                historyItems = historyItems,
                onDelete = viewModel::deleteHistoryItem,
                onSelect = onHistoryItemSelected,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}