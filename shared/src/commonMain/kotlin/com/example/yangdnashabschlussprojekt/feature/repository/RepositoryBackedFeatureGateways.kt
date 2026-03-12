package com.example.yangdnashabschlussprojekt.feature.repository

import com.example.yangdnashabschlussprojekt.data.model.HistoryRecord
import com.example.yangdnashabschlussprojekt.data.repository.HistoryRepository
import com.example.yangdnashabschlussprojekt.data.repository.UserRepository
import com.example.yangdnashabschlussprojekt.feature.model.SharedHistoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class RepositorySessionGateway(
    private val userRepository: UserRepository
) : SessionGateway {
    override val currentUser = userRepository.currentUser

    override suspend fun login(email: String, password: String): String? {
        return userRepository.login(email, password)
    }

    override suspend fun register(email: String, password: String, displayName: String): String? {
        return userRepository.register(email, password, displayName)
    }

    override suspend fun logout() {
        userRepository.logout()
    }
}

class RepositoryHistoryGateway(
    private val historyRepository: HistoryRepository
) : HistoryGateway {
    override val history: Flow<List<SharedHistoryItem>> = historyRepository.observeHistory()
        .map { records -> records.map(HistoryRecord::toUiItem) }

    override suspend fun clearAll() {
        historyRepository.clearAll()
    }

    override suspend fun delete(item: SharedHistoryItem) {
        historyRepository.delete(
            HistoryRecord(
                localId = item.localId,
                cloudId = item.cloudId,
                sourceText = item.recognizedText,
                translatedText = item.translatedText,
                timestamp = item.rawTimestamp,
                isFromCloud = item.isFromCloud
            )
        )
    }
}

class RepositoryCaptureGateway(
    private val historyRepository: HistoryRepository
) : CaptureGateway {
    override suspend fun saveCapture(recognizedText: String, translatedText: String): String? {
        return historyRepository.saveSnapshot(recognizedText, translatedText)
            .exceptionOrNull()
            ?.message
    }
}

private fun HistoryRecord.toUiItem(): SharedHistoryItem {
    return SharedHistoryItem(
        localId = localId,
        cloudId = cloudId,
        recognizedText = sourceText,
        translatedText = translatedText,
        timestampLabel = formatTimestamp(timestamp),
        rawTimestamp = timestamp,
        isFromCloud = isFromCloud
    )
}

private fun formatTimestamp(timestamp: Long): String {
    val dateTime = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')

    return "$day.$month.${dateTime.year} $hour:$minute"
}
