package com.example.yangdnashabschlussprojekt.feature.repository

import com.example.yangdnashabschlussprojekt.feature.model.SharedHistoryItem
import com.example.yangdnashabschlussprojekt.feature.model.SharedUser
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.module.Module
import org.koin.dsl.module

interface SessionGateway {
    val currentUser: Flow<SharedUser?>
    suspend fun login(email: String, password: String): String?
    suspend fun register(email: String, password: String, displayName: String): String?
    suspend fun logout()
}

interface OnboardingGateway {
    fun restartOnboarding()
}

interface HistoryGateway {
    val history: Flow<List<SharedHistoryItem>>
    suspend fun clearAll()
    suspend fun delete(item: SharedHistoryItem)
}

interface CaptureGateway {
    suspend fun saveCapture(recognizedText: String, translatedText: String): String?
}

private class DemoFeatureStore(platformLabel: String) {
    private var nextLocalId = 3L

    val currentUser = MutableStateFlow<SharedUser?>(
        SharedUser(
            id = "demo-$platformLabel",
            displayName = "$platformLabel Tester",
            email = "${platformLabel.lowercase()}@smartvision.demo",
            profileImageUrl = null
        )
    )

    val history = MutableStateFlow(
        listOf(
            SharedHistoryItem(
                localId = 1L,
                recognizedText = "Willkommen bei SmartVision auf $platformLabel",
                translatedText = "Welcome to SmartVision on $platformLabel",
                timestampLabel = "Heute 09:15",
                rawTimestamp = 1L
            ),
            SharedHistoryItem(
                localId = 2L,
                cloudId = "cloud-$platformLabel",
                recognizedText = "Kamera-Bridge aktiv",
                translatedText = "Camera bridge active",
                timestampLabel = "Heute 08:40",
                rawTimestamp = 2L,
                isFromCloud = true
            )
        )
    )

    fun addCapture(recognizedText: String, translatedText: String) {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        history.update { current ->
            listOf(
                SharedHistoryItem(
                    localId = nextLocalId++,
                    recognizedText = recognizedText,
                    translatedText = translatedText,
                    timestampLabel = formatDemoTimestamp(timestamp),
                    rawTimestamp = timestamp
                )
            ) + current
        }
    }
}

private class DemoSessionGateway(
    private val store: DemoFeatureStore
) : SessionGateway {
    override val currentUser: Flow<SharedUser?> = store.currentUser.asStateFlow()

    override suspend fun login(email: String, password: String): String? {
        return if (email.isBlank() || password.isBlank()) {
            "Bitte E-Mail und Passwort ausfuellen."
        } else {
            store.currentUser.value = SharedUser(
                id = email,
                displayName = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                email = email,
                profileImageUrl = null
            )
            null
        }
    }

    override suspend fun register(email: String, password: String, displayName: String): String? {
        return when {
            email.isBlank() || password.isBlank() || displayName.isBlank() -> {
                "Bitte alle Felder ausfuellen."
            }
            password.length < 6 -> {
                "Das Passwort muss mindestens 6 Zeichen haben."
            }
            else -> {
                store.currentUser.value = SharedUser(
                    id = email,
                    displayName = displayName,
                    email = email,
                    profileImageUrl = null
                )
                null
            }
        }
    }

    override suspend fun logout() {
        store.currentUser.value = null
    }
}

class DemoOnboardingGateway : OnboardingGateway {
    override fun restartOnboarding() {
    }
}

private class DemoHistoryGateway(
    private val store: DemoFeatureStore
) : HistoryGateway {
    override val history: Flow<List<SharedHistoryItem>> = store.history.asStateFlow()

    override suspend fun clearAll() {
        store.history.value = emptyList()
    }

    override suspend fun delete(item: SharedHistoryItem) {
        store.history.update { current ->
            current.filterNot { it.stableKey == item.stableKey }
        }
    }
}

private class DemoCaptureGateway(
    private val store: DemoFeatureStore
) : CaptureGateway {
    override suspend fun saveCapture(recognizedText: String, translatedText: String): String? {
        return if (recognizedText.isBlank()) {
            "Kein Text zum Speichern vorhanden."
        } else {
            store.addCapture(recognizedText = recognizedText, translatedText = translatedText)
            null
        }
    }
}

fun demoFeatureModule(platformLabel: String): Module = module {
    single { DemoFeatureStore(platformLabel) }
    single<SessionGateway> { DemoSessionGateway(get()) }
    single<OnboardingGateway> { DemoOnboardingGateway() }
    single<HistoryGateway> { DemoHistoryGateway(get()) }
    single<CaptureGateway> { DemoCaptureGateway(get()) }
}

private fun formatDemoTimestamp(timestamp: Long): String {
    val dateTime = Instant.fromEpochMilliseconds(timestamp)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')
    return "$day.$month.${dateTime.year} $hour:$minute"
}
