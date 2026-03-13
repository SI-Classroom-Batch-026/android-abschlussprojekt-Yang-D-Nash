package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.companion.DesktopCompanionServer
import com.example.yangdnashabschlussprojekt.feature.model.CompanionMode
import org.koin.compose.koinInject
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
actual fun PlatformArRoute(
    platformName: String,
    statusMessage: String?,
    onOpenCamera: () -> Unit,
    onOpenTextMode: () -> Unit,
    modifier: Modifier
) {
    DesktopCompanionRoute(
        focusMode = CompanionMode.AR,
        onPrimaryAction = onOpenTextMode,
        primaryActionLabel = "Zum Text-Companion wechseln",
        modifier = modifier
    )
}

@Composable
actual fun PlatformTextRoute(
    platformName: String,
    onOpenHistory: () -> Unit,
    modifier: Modifier
) {
    DesktopCompanionRoute(
        focusMode = CompanionMode.TEXT,
        onPrimaryAction = onOpenHistory,
        primaryActionLabel = "Verlauf oeffnen",
        modifier = modifier
    )
}

@Composable
private fun DesktopCompanionRoute(
    focusMode: CompanionMode,
    onPrimaryAction: () -> Unit,
    primaryActionLabel: String,
    modifier: Modifier = Modifier,
    server: DesktopCompanionServer = koinInject()
) {
    val state by server.state.collectAsState()
    val snapshot = state.lastSnapshot

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF001214), Color.Black)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = if (focusMode == CompanionMode.AR) "AR-Companion" else "Text-Companion",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.serverMessage,
                        color = Color.White.copy(alpha = 0.78f)
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "Desktop-Adresse",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    state.serverUrls.forEach { url ->
                        Text(
                            text = url,
                            color = Color(0xFF7DEBFF),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onPrimaryAction,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(primaryActionLabel)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Verbundene Session",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = state.lastDeviceName?.let { "Geraet: $it" } ?: "Noch kein Handy verbunden.",
                        color = Color.White
                    )
                    state.lastSeenAtEpochMillis?.let { timestamp ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Letztes Update: ${formatTimestamp(timestamp)}",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    snapshot?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Aktiver Modus: ${it.activeMode.name}",
                            color = Color(0xFF7DEBFF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (snapshot != null) {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Live-Spiegelung",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        snapshot.statusMessage?.takeIf { it.isNotBlank() }?.let { message ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = message,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        snapshot.recognizedObject?.takeIf { it.isNotBlank() }?.let { label ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Objekt",
                                color = Color.White.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = label,
                                color = Color(0xFF7DEBFF),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                            snapshot.objectCandidates
                                .filterNot { it.equals(label, ignoreCase = true) }
                                .take(5)
                                .forEach { candidate ->
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• $candidate",
                                        color = Color.White.copy(alpha = 0.82f)
                                    )
                                }
                        }
                        snapshot.recognizedText?.takeIf { it.isNotBlank() }?.let { text ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Erkannter Text",
                                color = Color.White.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = text,
                                color = Color.White
                            )
                        }
                        snapshot.translatedText?.takeIf { it.isNotBlank() }?.let { text ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Uebersetzung",
                                color = Color.White.copy(alpha = 0.75f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = text,
                                color = Color(0xFFFFD166)
                            )
                        }
                    }
                }
            }

            if (state.mirroredHistory.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Gesendeter Verlauf",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        state.mirroredHistory.take(5).forEach { entry ->
                            Text(
                                text = formatTimestamp(entry.timestampMillis),
                                color = Color(0xFF7DEBFF),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = entry.recognizedText,
                                color = Color.White
                            )
                            entry.translatedText.takeIf { it.isNotBlank() }?.let { translated ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = translated,
                                    color = Color.White.copy(alpha = 0.72f)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(18.dp))
                TextButton(onClick = onPrimaryAction) {
                    Text(
                        text = "Sobald dein Handy Daten sendet, erscheinen hier Text, Objekte und Verlauf live.",
                        color = Color.White.copy(alpha = 0.68f)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestampMillis: Long): String {
    return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(timestampMillis))
}
