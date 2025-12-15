package com.example.yangdnashabschlussprojekt.ui.component.user.registration

import androidx.compose.material3.MaterialTheme // NEU: Import für MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier // Fügen Sie einen optionalen Modifier hinzu (Best Practice)
import com.example.yangdnashabschlussprojekt.data.remote.RegistrationResult

@Composable
fun RegistrationFeedback(result: RegistrationResult?, modifier: Modifier = Modifier) {
    result?.let {
        when {
            it.success -> {
                Text(
                    "Erfolgreich registriert!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = modifier
                )
            }
            !it.errorMessage.isNullOrEmpty() -> {
                Text(
                    "Fehler: ${it.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = modifier
                )
            }
        }
    }
}