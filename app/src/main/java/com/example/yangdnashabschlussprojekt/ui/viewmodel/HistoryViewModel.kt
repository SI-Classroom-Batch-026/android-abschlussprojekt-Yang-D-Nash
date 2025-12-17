package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.ui.component.history.HistoryItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val historyState: StateFlow<List<HistoryItem>> = historyRepository.getAllHistory()
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
            historyRepository.clearHistory()
        }
    }
    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date)
    }
    fun deleteHistoryItem(itemId: Long) {
        viewModelScope.launch {
            historyRepository.deleteEntryById(itemId)
        }
    }
}