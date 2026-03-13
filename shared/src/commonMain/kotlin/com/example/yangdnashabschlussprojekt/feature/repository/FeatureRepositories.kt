package com.example.yangdnashabschlussprojekt.feature.repository

import com.example.yangdnashabschlussprojekt.feature.model.SharedHistoryItem
import com.example.yangdnashabschlussprojekt.feature.model.SharedUser
import kotlinx.coroutines.flow.Flow

interface SessionGateway {
    val currentUser: Flow<SharedUser?>
    suspend fun login(email: String, password: String): String?
    suspend fun register(email: String, password: String, displayName: String): String?
    suspend fun logout()
}

interface OnboardingGateway {
    val isOnboardingComplete: Flow<Boolean>
    fun completeOnboarding()
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
