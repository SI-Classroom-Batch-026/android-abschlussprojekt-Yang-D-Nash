package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth().verticalScroll(rememberScrollState())) {
            Text("Analyse Ergebnis", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Übersetzung:", style = MaterialTheme.typography.labelSmall)
                    Text(translatedText.ifBlank { "Wird übersetzt..." })
                }
            }
            OutlinedTextField(
                value = editableText,
                onValueChange = { editableText = it },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onTextEdited(editableText) }, modifier = Modifier.weight(1f)) { Text("Update") }
                Button(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Fertig") }
            }
            Button(
                onClick = onSaveToCloud,
                enabled = isLoggedIn,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(if (isLoggedIn) "In Cloud sichern" else "Login nötig")
            }
        }
    }
}