package com.example.yangdnashabschlussprojekt.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.koin.compose.KoinContext

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            androidx.compose.material3.Scaffold(
                modifier = androidx.compose.ui.Modifier.fillMaxSize()
            ) { padding ->
                androidx.compose.foundation.layout.Box(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text("Hallo vom Shared Modul! Deine App läuft.")
                }
            }
        }
    }
}