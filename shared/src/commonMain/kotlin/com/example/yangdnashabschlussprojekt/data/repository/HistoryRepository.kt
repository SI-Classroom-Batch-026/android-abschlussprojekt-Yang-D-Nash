package com.example.yangdnashabschlussprojekt.data.repository

import com.example.yangdnashabschlussprojekt.data.source.IHistoryDataSource
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val dataSource: IHistoryDataSource
) {
    fun getAllHistory(): Flow<List<TextHistory>> = dataSource.getAllHistory()
    
    suspend fun saveEntry(entry: TextHistory) = dataSource.saveEntry(entry)
    
    suspend fun deleteEntryById(id: Long) = dataSource.deleteEntryById(id)
    
    suspend fun clearHistory() = dataSource.clearHistory()
}