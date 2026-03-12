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
    showRegisterAction: Boolean = true
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF001214), Color.Black)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBack != null) {
                    TextButton(onClick = onBack) {
                        Text("Zurueck", color = Color.White)
                    }
                } else {
                    Spacer(modifier = Modifier.height(1.dp))
                }
                Text(
                    text = "Einstellungen",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(1.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentUser?.displayName ?: "Gast",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentUser?.email ?: "Noch nicht angemeldet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
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
                            Text("Abmelden", color = Color(0xFFFF7A7A))
                        }
                    }
                }
            }

            if (authMessage != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF7DEBFF).copy(alpha = 0.14f)
                ) {
                    Text(
                        text = authMessage,
                        modifier = Modifier.padding(16.dp),
                        color = Color.White
                    )
                }
            }

            if (currentUser == null) {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Anmeldung",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
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

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.05f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Systemstatus",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
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
        Text(text = label, color = Color.White)
        Text(
            text = if (enabled) "Aktiv" else "Aus",
            color = if (enabled) Color(0xFF7DEBFF) else Color.White.copy(alpha = 0.55f),
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}
