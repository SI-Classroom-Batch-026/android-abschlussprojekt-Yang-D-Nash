package com.example.yangdnashabschlussprojekt.data.local.source

// Diese kommen aus dem Shared-Modul (das ist erlaubt)

import com.example.yangdnashabschlussprojekt.data.local.database.dao.TextHistoryDao
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.toEntity
import com.example.yangdnashabschlussprojekt.data.local.database.model.box.toKmpModel
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import com.example.yangdnashabschlussprojekt.domain.usecase.IHistoryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HistoryDataSourceImpl(
    private val dao: TextHistoryDao
) : IHistoryDataSource {
    override fun getAllHistory(): Flow<List<TextHistory>> {
        return dao.getAllHistory().map { entityList ->
            entityList.map { it.toKmpModel() }
        }
    }
    override suspend fun saveEntry(history: TextHistory) {
        dao.insert(history.toEntity())
    }
    override suspend fun deleteEntryById(id: Long) {
        dao.deleteById(id)
    }
    override suspend fun clearHistory() {
        dao.deleteAll()
    }
}