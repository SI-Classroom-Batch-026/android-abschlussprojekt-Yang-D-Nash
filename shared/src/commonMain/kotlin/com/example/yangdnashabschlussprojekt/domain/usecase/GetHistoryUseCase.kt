package com.example.yangdnashabschlussprojekt.domain.usecase

import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import kotlinx.coroutines.flow.Flow

class GetHistoryUseCase(private val repository: HistoryRepository) {
    operator fun invoke(): Flow<List<TextHistory>> = repository.getAllHistory()
}