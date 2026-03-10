package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.HistoryItem
import com.example.yangdnashabschlussprojekt.domain.usecase.GetHistoryUseCase
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryViewModel(
    getHistoryUseCase: GetHistoryUseCase,
    private val manageHistoryUseCase: ManageHistoryUseCase
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val historyState: StateFlow<List<HistoryItem>> = getHistoryUseCase()
        .map { models ->
            models.map { model ->
                HistoryItem(
                    id = model.id ?: 0L,
                    recognizedText = model.sourceText,
                    translatedText = model.translatedText,
                    timestampFormatted = formatTimestamp(model.timestamp),
                    rawTimestamp = model.timestamp
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun clearAllHistory() {
        viewModelScope.launch {
            manageHistoryUseCase.clear()
        }
    }
    fun deleteHistoryItem(item: HistoryItem) {
        viewModelScope.launch {
            item.id?.let { localId ->
                manageHistoryUseCase.delete(localId)
            }
        }
    }
    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return dateFormatter.format(date)
    }
}