package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.yangdnashabschlussprojekt.feature.model.SharedHistoryItem
import com.example.yangdnashabschlussprojekt.feature.ui.SharedHistoryScreen
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedHistoryViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HistoryScreen(
    viewModel: SharedHistoryViewModel = koinViewModel(),
    onBack: () -> Unit,
    onHistoryItemSelected: (SharedHistoryItem) -> Unit
) {
    val historyItems by viewModel.historyState.collectAsState()

    SharedHistoryScreen(
        historyItems = historyItems,
        onDelete = viewModel::deleteHistoryItem,
        onClearAll = viewModel::clearAllHistory,
        onOpenItem = onHistoryItemSelected,
        onBack = onBack
    )
}
