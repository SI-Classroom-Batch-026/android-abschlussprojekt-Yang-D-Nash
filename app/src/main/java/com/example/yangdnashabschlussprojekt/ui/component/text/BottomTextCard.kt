package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomTextCard(
    recognizedText: String,
    translatedText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 250.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        val scrollState = rememberScrollState()
        SelectionContainer {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {
                Text("Erkannter Text:", color = Color.White)
                Text(recognizedText.ifBlank { "Noch kein Text erkannt" }, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Übersetzt:", color = Color.White)
                Text(translatedText.ifBlank { "Noch keine Übersetzung" }, color = Color.White)
            }
        }
    }
}
