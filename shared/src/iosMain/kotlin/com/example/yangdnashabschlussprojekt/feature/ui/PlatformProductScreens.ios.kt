package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformArRoute(
    platformName: String,
    statusMessage: String?,
    onOpenCamera: () -> Unit,
    onOpenTextMode: () -> Unit,
    modifier: Modifier
) {
    SharedArRoute(
        onOpenTextMode = onOpenTextMode,
        modifier = modifier
    )
}

@Composable
actual fun PlatformTextRoute(
    platformName: String,
    onOpenHistory: () -> Unit,
    modifier: Modifier
) {
    SharedCaptureRoute(
        onOpenHistory = onOpenHistory,
        modifier = modifier
    )
}
