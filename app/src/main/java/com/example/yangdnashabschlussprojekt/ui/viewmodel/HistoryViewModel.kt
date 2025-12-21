package com.example.yangdnashabschlussprojekt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yangdnashabschlussprojekt.domain.usecase.GetHistoryUseCase
import com.example.yangdnashabschlussprojekt.domain.usecase.ManageHistoryUseCase
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.HistoryItem
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
            // Lösche lokal, wenn eine Room-ID da ist
            item.id?.let { localId ->
                manageHistoryUseCase.delete(localId)
            }

            // Lösche aus der Cloud, wenn eine Firestore-ID da ist
            item.firestoreId?.let { cloudId ->
                // Falls du diese Methode noch nicht hast:
                // repository.deleteFromCloud(cloudId)
            }
        }
    }
    private fun formatTimestamp(timestamp: Long): String {
        val date = Date(timestamp)
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(date)
    }
}