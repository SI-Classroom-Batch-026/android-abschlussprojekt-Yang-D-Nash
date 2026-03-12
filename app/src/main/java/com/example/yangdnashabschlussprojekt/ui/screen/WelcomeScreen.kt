package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.yangdnashabschlussprojekt.feature.ui.SharedWelcomeScreen
import com.example.yangdnashabschlussprojekt.feature.viewmodel.SharedWelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeScreen(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: SharedWelcomeViewModel = koinViewModel(),
    onNavigateToOnboarding: () -> Unit
) {
    val displayName by viewModel.displayName.collectAsState()

    SharedWelcomeScreen(
        displayName = displayName,
        contentPadding = contentPadding,
        onRestartOnboarding = {
            viewModel.restartOnboarding()
            onNavigateToOnboarding()
        }
    )
}
