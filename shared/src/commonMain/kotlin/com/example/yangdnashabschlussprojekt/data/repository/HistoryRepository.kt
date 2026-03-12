package com.example.yangdnashabschlussprojekt.data.repository

import com.example.yangdnashabschlussprojekt.data.model.HistoryRecord
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

interface LocalHistoryStore {
    fun observeHistory(): Flow<List<HistoryRecord>>
    suspend fun save(record: HistoryRecord)
    suspend fun deleteById(id: Long)
    suspend fun clear()
}

class HistoryRepository(
    private val localHistoryStore: LocalHistoryStore,
    private val userRepository: UserRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeHistory(): Flow<List<HistoryRecord>> {
        val cloudHistory = userRepository.currentUser.flatMapLatest { user ->
            if (user == null) {
                flowOf(emptyList())
            } else {
                flow { emit(userRepository.getCloudHistory()) }
            }
        }

        return combine(localHistoryStore.observeHistory(), cloudHistory) { local, cloud ->
            (local + cloud)
                .distinctBy { record ->
                    record.cloudId ?: "local-${record.localId ?: record.timestamp}-${record.sourceText}"
                }
                .sortedByDescending { it.timestamp }
        }
    }

    suspend fun saveSnapshot(
        sourceText: String,
        translatedText: String
    ): Result<Unit> {
        if (sourceText.isBlank()) {
            return Result.failure(IllegalArgumentException("Kein Text zum Speichern vorhanden."))
        }

        val timestamp = Clock.System.now().toEpochMilliseconds()
        localHistoryStore.save(
            HistoryRecord(
                sourceText = sourceText,
                translatedText = translatedText,
                timestamp = timestamp
            )
        )

        return userRepository.saveHistoryEntry(
            sourceText = sourceText,
            translatedText = translatedText,
            timestamp = timestamp
        ).fold(
            onSuccess = { Result.success(Unit) },
            onFailure = {
                Result.failure(Exception("Verlauf lokal gespeichert, Cloud-Backup fehlgeschlagen."))
            }
        )
    }

    suspend fun delete(record: HistoryRecord) {
        record.localId?.let { localHistoryStore.deleteById(it) }
        record.cloudId?.let { userRepository.deleteCloudHistoryEntry(it) }
    }

    suspend fun clearAll() {
        localHistoryStore.clear()
        userRepository.clearCloudHistory()
    }
}
