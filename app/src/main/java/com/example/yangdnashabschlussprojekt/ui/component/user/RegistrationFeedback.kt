package com.example.yangdnashabschlussprojekt.ui.component.user

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.yangdnashabschlussprojekt.data.model.RegistrationResult

@Composable
fun RegistrationFeedback(result: RegistrationResult?) {
    result?.let {
        when {
            it.success -> Text("Erfolgreich registriert!", color = Color.Green)
            !it.errorMessage.isNullOrEmpty() -> Text("Fehler: ${it.errorMessage}", color = Color.Red)
        }
    }
}
