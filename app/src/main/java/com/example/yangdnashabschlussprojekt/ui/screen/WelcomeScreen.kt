package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.yangdnashabschlussprojekt.ui.component.welcome.SettingsButton
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeGreeting
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeImage
import com.example.yangdnashabschlussprojekt.ui.viewmodel.WelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel = koinViewModel(),
    onOpenSettings: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    val displayName = currentUser?.displayName ?: "Gast"
    val isNotLoggedIn = displayName == "Gast"

    // Animation-States
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true } // Triggert Animation beim Start

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                // Subtiler Gradient für mehr Tiefe
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    )
                )
            )
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Das Bild schwebt sanft ein
        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(animationSpec = tween(1000)) + expandVertically()
        ) {
            WelcomeImage()
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Begrüßung mit Slide-In Effekt
        AnimatedVisibility(
            visible = visible.value,
            enter = slideInVertically(initialOffsetY = { 40 }) + fadeIn(animationSpec = tween(800, delayMillis = 300))
        ) {
            WelcomeGreeting(displayName)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Buttons erscheinen versetzt (Staggered)
        AnimatedVisibility(
            visible = visible.value,
            enter = slideInVertically(initialOffsetY = { 60 }) + fadeIn(animationSpec = tween(800, delayMillis = 500))
        ) {
            OutlinedButton(
                onClick = {
                    viewModel.startOnboardingAgain()
                    onNavigateToOnboarding()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp) // Modernere Ecken
            ) {
                Text("Anleitung (Onboarding) zeigen", modifier = Modifier.padding(8.dp))
            }
        }

        if (isNotLoggedIn) {
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 700))
            ) {
                SettingsButton(onClick = onOpenSettings)
            }
        }
    }
}