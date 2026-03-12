package com.example.yangdnashabschlussprojekt.feature.model

data class SharedUser(
    val id: String,
    val displayName: String,
    val email: String?,
    val profileImageUrl: String?
)

data class SharedHistoryItem(
    val localId: Long? = null,
    val cloudId: String? = null,
    val recognizedText: String,
    val translatedText: String,
    val timestampLabel: String,
    val rawTimestamp: Long,
    val isFromCloud: Boolean = false
) {
    val stableKey: String
        get() = cloudId ?: localId?.toString() ?: rawTimestamp.toString()
}

data class SettingsPermissionSnapshot(
    val notificationsEnabled: Boolean,
    val cameraGranted: Boolean,
    val locationGranted: Boolean,
    val microphoneGranted: Boolean
)
