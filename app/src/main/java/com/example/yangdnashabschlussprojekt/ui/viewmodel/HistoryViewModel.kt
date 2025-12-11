package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
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

    val historyState: StateFlow<List<HistoryItem>> = historyRepository.getHistory()
        .map { entities ->
            entities.map { entity ->
                HistoryItem(
                    id = entity.id,
                    recognizedText = entity.recognizedText,
                    translatedText = entity.translatedText,
                    timestampFormatted = formatTimestamp(entity.timestamp),
                    rawTimestamp = entity.timestamp
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date)
    }
    
    fun deleteHistoryItem(itemId: Long) {
        viewModelScope.launch {
            historyRepository.deleteEntryById(itemId)
        }
    }
    
    fun clearAllHistory() {
        viewModelScope.launch {
            historyRepository.clearHistory()
        }
    }
}