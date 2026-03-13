package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.feature.model.SettingsPermissionSnapshot
import com.example.yangdnashabschlussprojekt.feature.model.SharedUser
import com.example.yangdnashabschlussprojekt.shared.SmartVisionAccentCard
import com.example.yangdnashabschlussprojekt.shared.SmartVisionGlassCard
import com.example.yangdnashabschlussprojekt.shared.SmartVisionHeader
import com.example.yangdnashabschlussprojekt.shared.SmartVisionScreenBackground

@Composable
fun SharedSettingsScreen(
    currentUser: SharedUser?,
    authMessage: String?,
    permissions: SettingsPermissionSnapshot,
    onLogin: (String, String) -> Unit,
    onLogout: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenRegister: () -> Unit,
    onOpenSystemSettings: () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    showRegisterAction: Boolean = true,
    companionHost: String? = null,
    companionStatusMessage: String? = null,
    onCompanionHostChange: ((String) -> Unit)? = null,
    onConnectCompanion: (() -> Unit)? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .then(SmartVisionScreenBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SmartVisionHeader(
                title = "Einstellungen",
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(20.dp))

            SmartVisionGlassCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentUser?.displayName ?: "Gast",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentUser?.email ?: "Noch nicht angemeldet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    if (currentUser != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onOpenHistory,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text("Verlauf oeffnen")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = onLogout) {
                            Text("Abmelden", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            if (authMessage != null) {
                Spacer(modifier = Modifier.height(20.dp))
                SmartVisionAccentCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = authMessage,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (currentUser == null) {
                Spacer(modifier = Modifier.height(24.dp))
                SmartVisionGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Anmeldung",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("E-Mail") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Passwort") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onLogin(email, password) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text("Einloggen")
                        }
                        if (showRegisterAction) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = onOpenRegister,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Text("Registrieren")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

                SmartVisionGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Systemstatus",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    Spacer(modifier = Modifier.height(16.dp))
                    PermissionRow("Benachrichtigungen", permissions.notificationsEnabled)
                    PermissionRow("Kamera", permissions.cameraGranted)
                    PermissionRow("Standort", permissions.locationGranted)
                    PermissionRow("Mikrofon", permissions.microphoneGranted)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onOpenSystemSettings,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Systemeinstellungen oeffnen")
                    }
                }
            }

            if (companionHost != null && onCompanionHostChange != null && onConnectCompanion != null) {
                Spacer(modifier = Modifier.height(24.dp))
                SmartVisionGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Desktop Companion",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Trage die Desktop-Adresse ein, damit SmartVision AR-, Text- und Verlaufsdaten live an deinen Rechner spiegeln kann.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = companionHost,
                            onValueChange = onCompanionHostChange,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Desktop-Adresse") },
                            singleLine = true
                        )
                        companionStatusMessage?.takeIf { it.isNotBlank() }?.let { message ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = message,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onConnectCompanion,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Text("Desktop verbinden")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun PermissionRow(label: String, enabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = MaterialTheme.colorScheme.onBackground)
        Text(
            text = if (enabled) "Aktiv" else "Aus",
            color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}
