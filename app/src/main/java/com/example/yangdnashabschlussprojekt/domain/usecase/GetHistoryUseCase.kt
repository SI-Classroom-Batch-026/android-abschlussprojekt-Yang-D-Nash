package com.example.yangdnashabschlussprojekt.domain.usecase

import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetHistoryUseCase(private val repository: HistoryRepository) {

    operator fun invoke(): Flow<List<TextHistory>> {
        val localFlow = repository.getAllHistory()

        val cloudFlow = flow {
            val cloudData = repository.getCloudHistory()
            emit(cloudData)
        }

        return localFlow.combine(cloudFlow) { local, cloud ->
            (local + cloud)
                .distinctBy { it.sourceText + it.timestamp } // Duplikate vermeiden
                .sortedByDescending { it.timestamp }         // Neueste zuerst
        }
    }
}