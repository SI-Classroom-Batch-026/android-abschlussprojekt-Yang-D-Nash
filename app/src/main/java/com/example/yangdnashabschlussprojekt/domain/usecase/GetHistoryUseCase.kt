package com.example.yangdnashabschlussprojekt.domain.usecase

import com.example.yangdnashabschlussprojekt.data.remote.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

class GetHistoryUseCase(private val repository: HistoryRepository) {
    operator fun invoke(): Flow<List<TextHistory>> {
        val localFlow = repository.getAllHistory()
        val cloudFlow = flow { emit(repository.getCloudHistory()) }

        return localFlow.combine(cloudFlow) { local, cloud ->
            (local + cloud)
                .distinctBy { it.sourceText to it.timestamp }
                .sortedByDescending { it.timestamp }
        }
    }
}