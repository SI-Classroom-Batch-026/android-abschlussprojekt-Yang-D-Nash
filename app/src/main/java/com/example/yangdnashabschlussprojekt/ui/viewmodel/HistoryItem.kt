package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryItem(
    val id: Long,
    val recognizedText: String,
    val translatedText: String,
    val timestampFormatted: String, // Für die Anzeige
    val rawTimestamp: Long
)

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
        val date = java.util.Date(timestamp)
        return java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(date)
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