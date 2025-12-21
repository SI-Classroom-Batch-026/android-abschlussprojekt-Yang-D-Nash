package com.example.yangdnashabschlussprojekt.domain.usecase

import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory

class ManageHistoryUseCase(private val repository: HistoryRepository, private val userRepository: UserRepository) {
    suspend fun save(entry: TextHistory) = repository.saveEntry(entry)
    suspend fun saveToCloud(source: String, translated: String) {
        repository.saveToCloud(source, translated)
    }
    suspend fun delete(id: Long) {
        repository.deleteEntryById(id)
    }
    suspend fun clear() = repository.clearHistory()
}