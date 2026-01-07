package com.example.yangdnashabschlussprojekt.ui.component.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.R

@Composable
fun EmptyHistoryMessage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Color.White.copy(alpha = 0.2f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.history_empty_title),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(R.string.history_empty_desc),
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}