package com.example.yangdnashabschlussprojekt.data.repository

import com.example.yangdnashabschlussprojekt.data.model.HistoryRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InMemoryLocalHistoryStore : LocalHistoryStore {
    private val records = MutableStateFlow<List<HistoryRecord>>(emptyList())
    private var nextId = 1L

    override fun observeHistory(): Flow<List<HistoryRecord>> = records.asStateFlow()

    override suspend fun save(record: HistoryRecord) {
        val storedRecord = if (record.localId == null) {
            record.copy(localId = nextId++)
        } else {
            record
        }

        records.update { current ->
            listOf(storedRecord) + current.filterNot { it.localId == storedRecord.localId }
        }
    }

    override suspend fun deleteById(id: Long) {
        records.update { current -> current.filterNot { it.localId == id } }
    }

    override suspend fun clear() {
        records.value = emptyList()
    }
}
