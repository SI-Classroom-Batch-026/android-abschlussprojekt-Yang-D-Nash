package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.data.local.repository.SettingsRepository
import com.example.yangdnashabschlussprojekt.data.model.HistoryRecord
import com.example.yangdnashabschlussprojekt.data.repository.LocalHistoryStore
import com.example.yangdnashabschlussprojekt.domain.model.TextHistory
import com.example.yangdnashabschlussprojekt.domain.usecase.IHistoryDataSource
import com.example.yangdnashabschlussprojekt.feature.repository.OnboardingGateway
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class AndroidOnboardingGateway(
    private val settingsRepository: SettingsRepository
) : OnboardingGateway {
    private val onboardingState = MutableStateFlow(settingsRepository.isOnboardingComplete())

    override val isOnboardingComplete: Flow<Boolean> = onboardingState.asStateFlow()

    override fun completeOnboarding() {
        settingsRepository.setOnboardingComplete(true)
        onboardingState.value = true
    }

    override fun restartOnboarding() {
        settingsRepository.setOnboardingComplete(false)
        onboardingState.value = false
    }
}

class AndroidLocalHistoryStore(
    private val dataSource: IHistoryDataSource
) : LocalHistoryStore {
    override fun observeHistory(): Flow<List<HistoryRecord>> {
        return dataSource.getAllHistory().map { entries ->
            entries.map { entry ->
                HistoryRecord(
                    localId = entry.id,
                    sourceText = entry.sourceText,
                    translatedText = entry.translatedText,
                    timestamp = entry.timestamp
                )
            }
        }
    }

    override suspend fun save(record: HistoryRecord) {
        dataSource.saveEntry(
            TextHistory(
                id = record.localId,
                sourceText = record.sourceText,
                translatedText = record.translatedText,
                timestamp = record.timestamp
            )
        )
    }

    override suspend fun deleteById(id: Long) {
        dataSource.deleteEntryById(id)
    }

    override suspend fun clear() {
        dataSource.clearHistory()
    }
}
