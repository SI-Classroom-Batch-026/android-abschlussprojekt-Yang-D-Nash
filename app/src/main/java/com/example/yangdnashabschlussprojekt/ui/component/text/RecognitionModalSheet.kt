package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognitionModalSheet(
    recognizedText: String,
    translatedText: String,
    onDismiss: () -> Unit,
    onTextEdited: (String) -> Unit,
    onSaveToCloud: () -> Unit,
    isLoggedIn: Boolean
) {
    var editableText by remember { mutableStateOf(recognizedText) }
    val speak = rememberTextToSpeech()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF121212),
        scrimColor = Color.Black.copy(alpha = 0.7f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "ÜBERSETZUNG",
                    color = Color(0xFFFF00FF),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(
                    onClick = { speak(translatedText) },
                    enabled = translatedText.isNotBlank(),
                    modifier = Modifier.background(Color(0xFFFF00FF).copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Vorlesen",
                        tint = if (translatedText.isNotBlank()) Color(0xFFFF00FF) else Color.Gray
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = translatedText.ifBlank { "Übersetzung läuft..." },
                    color = Color.White,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("ORIGINALTEXT EDITIEREN", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            OutlinedTextField(
                value = editableText,
                onValueChange = { editableText = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFFFF00FF),
                    cursorColor = Color(0xFFFF00FF)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { onTextEdited(editableText) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF00FF))
                ) { Text("UPDATE", color = Color(0xFFFF00FF)) }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF00FF)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("FERTIG", fontWeight = FontWeight.Bold, color = Color.White) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSaveToCloud,
                enabled = isLoggedIn,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.1f),
                    disabledContainerColor = Color.White.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    if (isLoggedIn) "☁️ In Cloud sichern" else "Login für Cloud-Backup nötig",
                    color = if (isLoggedIn) Color.White else Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}