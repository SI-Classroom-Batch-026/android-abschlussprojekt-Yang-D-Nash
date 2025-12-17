package com.example.yangdnashabschlussprojekt.domain.usecase

import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory

class ManageHistoryUseCase(private val repository: HistoryRepository) {
    suspend fun save(entry: TextHistory) = repository.saveEntry(entry)
    suspend fun delete(id: Long) = repository.deleteEntryById(id)
    suspend fun clear() = repository.clearHistory()
}