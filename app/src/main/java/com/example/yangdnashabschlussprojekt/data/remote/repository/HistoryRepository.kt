package com.example.yangdnashabschlussprojekt.data.remote.repository

import com.example.yangdnashabschlussprojekt.data.local.database.dao.TextHistoryDao
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.TextHistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(
    private val textHistoryDao: TextHistoryDao
) {

    suspend fun saveEntry(entity: TextHistoryEntity) {
        textHistoryDao.insert(entity)
    }

    fun getHistory(): Flow<List<TextHistoryEntity>> {
        return textHistoryDao.getAllHistory()
    }

    suspend fun deleteEntryById(id: Long) {
        textHistoryDao.deleteById(id)
    }
    suspend fun clearHistory() {
        textHistoryDao.deleteAll()
    }
}