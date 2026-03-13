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
    SharedModeOverviewScreen(
        title = "AR-Modus",
        description = "Android nutzt weiterhin den nativen Live-AR-Flow im App-Modul. Die Shared-App zeigt hier nur dieselbe Produktstruktur.",
        detailTitle = "Android-Quelle",
        detailBody = "Die echte Objekterkennung, Bounding Boxes, Uebersetzung und Cloud-Snapshots bleiben auf Android in den nativen Screens und ViewModels verankert.",
        statusMessage = statusMessage,
        primaryActionLabel = "Kamera oeffnen",
        onPrimaryAction = onOpenCamera,
        secondaryActionLabel = "Zum Text-Scanner wechseln",
        onSecondaryAction = onOpenTextMode,
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
