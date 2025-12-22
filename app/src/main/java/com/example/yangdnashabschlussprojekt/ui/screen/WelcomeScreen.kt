package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.ui.component.welcome.SettingsButton
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeGreeting
import com.example.yangdnashabschlussprojekt.ui.component.welcome.WelcomeImage
import com.example.yangdnashabschlussprojekt.ui.viewmodel.AndroidWelcomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun WelcomeScreen(
    viewModel: AndroidWelcomeViewModel = koinViewModel(),
    onOpenSettings: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState(initial = null)
    val displayName = currentUser?.displayName ?: "Gast"
    val isNotLoggedIn = displayName == "Gast"
    val visible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible.value = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF001214), Color(0xFF000000))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                // FIX: bottom padding auf 120.dp gesetzt, damit die Buttons ÜBER der Bar landen
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 120.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo Animation
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(tween(1200)) + scaleIn(initialScale = 0.9f)
            ) {
                WelcomeImage()
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Greeting Animation
            AnimatedVisibility(
                visible = visible.value,
                enter = slideInVertically { 40 } + fadeIn(tween(800, 300))
            ) {
                WelcomeGreeting(displayName)
            }

            // Dieser Spacer drückt alles Folgende nach unten
            Spacer(modifier = Modifier.weight(1f))

            // Onboarding Button
            AnimatedVisibility(
                visible = visible.value,
                enter = slideInVertically { 60 } + fadeIn(tween(800, 500))
            ) {
                Button(
                    onClick = {
                        viewModel.startOnboardingAgain()
                        onNavigateToOnboarding()
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("Anleitung starten", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }
            }

            if (isNotLoggedIn) {
                Spacer(modifier = Modifier.height(16.dp))
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(800, 700)) + slideInVertically { 20 }
                ) {
                    SettingsButton(onClick = onOpenSettings)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}