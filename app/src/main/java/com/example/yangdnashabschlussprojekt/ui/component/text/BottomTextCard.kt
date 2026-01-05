package com.example.yangdnashabschlussprojekt.ui.component.text

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BottomTextCard(
    modifier: Modifier = Modifier,
    recognizedText: String,
    isSingleBlock: Boolean = false,
    bottomPadding: androidx.compose.ui.unit.Dp = 116.dp
) {
    if (recognizedText.isNotBlank()) {
        Card(
            modifier = modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = bottomPadding)
                .fillMaxWidth()
                .heightIn(max = 200.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
            border = BorderStroke(1.dp, if(isSingleBlock) Color(0xFF00FFCC) else Color.White.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(
                        text = if (isSingleBlock) "TARGET ACQUIRED" else "FULL SCAN RESULTS",
                        color = if (isSingleBlock) Color(0xFF00FFCC) else Color.Cyan,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                val scrollState = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Text(
                        text = recognizedText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}