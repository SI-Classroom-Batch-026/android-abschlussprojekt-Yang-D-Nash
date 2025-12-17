package com.example.yangdnashabschlussprojekt.data.source

import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import kotlinx.coroutines.flow.Flow

interface IHistoryDataSource {
    fun getAllHistory(): Flow<List<TextHistory>>
    suspend fun saveEntry(history: TextHistory)
    suspend fun deleteEntryById(id: Long)
    suspend fun clearHistory()
}