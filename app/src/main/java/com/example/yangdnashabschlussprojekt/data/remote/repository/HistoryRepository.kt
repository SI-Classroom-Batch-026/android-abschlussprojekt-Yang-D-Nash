package com.example.yangdnashabschlussprojekt.data.remote.repository

import com.example.yangdnashabschlussprojekt.domain.usecase.IHistoryDataSource
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val dataSource: IHistoryDataSource,
    private val userRepository: UserRepository
) {
    fun getAllHistory(): Flow<List<TextHistory>> = dataSource.getAllHistory()
    suspend fun getCloudHistory(): List<TextHistory> {
        return userRepository.getCloudHistory().map { map ->
            TextHistory(
                id = null,
                sourceText = map["sourceText"] as? String ?: "",
                translatedText = map["translatedText"] as? String ?: "",
                timestamp = map["timestamp"] as? Long ?: 0L
            )
        }
    }
    suspend fun saveToCloud(source: String, target: String) {
        userRepository.saveToFirestore(source, target)
    }
    suspend fun saveEntry(entry: TextHistory) = dataSource.saveEntry(entry)
    suspend fun deleteEntryById(id: Long) = dataSource.deleteEntryById(id)
    suspend fun clearHistory() = dataSource.clearHistory()
}