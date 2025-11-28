package com.example.yangdnashabschlussprojekt.ui.component.user

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RegistrationFeedback(result: Pair<Boolean, String?>) {
    val (success, error) = result
    when {
        success -> Text("Erfolgreich registriert!", color = Color.Green)
        !error.isNullOrEmpty() -> Text("Fehler: $error", color = Color.Red)
    }
}
