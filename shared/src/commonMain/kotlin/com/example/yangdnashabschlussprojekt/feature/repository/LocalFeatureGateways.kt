package com.example.yangdnashabschlussprojekt.feature.repository

import com.example.yangdnashabschlussprojekt.data.model.HistoryRecord
import com.example.yangdnashabschlussprojekt.data.repository.LocalHistoryStore
import com.example.yangdnashabschlussprojekt.feature.model.SharedHistoryItem
import com.example.yangdnashabschlussprojekt.feature.model.SharedUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class UnavailableSessionGateway(
    private val unavailableMessage: String
) : SessionGateway {
    private val sessionState = MutableStateFlow<SharedUser?>(null)

    override val currentUser: Flow<SharedUser?> = sessionState.asStateFlow()

    override suspend fun login(email: String, password: String): String? = unavailableMessage

    override suspend fun register(email: String, password: String, displayName: String): String? {
        return unavailableMessage
    }

    override suspend fun logout() {
        sessionState.value = null
    }
}

class LocalHistoryGateway(
    private val localHistoryStore: LocalHistoryStore
) : HistoryGateway {
    override val history: Flow<List<SharedHistoryItem>> = localHistoryStore.observeHistory()
        .map { records -> records.map(HistoryRecord::toUiItem) }

    override suspend fun clearAll() {
        localHistoryStore.clear()
    }

    override suspend fun delete(item: SharedHistoryItem) {
        item.localId?.let { localId ->
            localHistoryStore.deleteById(localId)
        }
    }
}

class LocalCaptureGateway(
    private val localHistoryStore: LocalHistoryStore
) : CaptureGateway {
    override suspend fun saveCapture(recognizedText: String, translatedText: String): String? {
        if (recognizedText.isBlank()) {
            return "Kein Text zum Speichern vorhanden."
        }

        localHistoryStore.save(
            HistoryRecord(
                sourceText = recognizedText,
                translatedText = translatedText,
                timestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
        return null
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
