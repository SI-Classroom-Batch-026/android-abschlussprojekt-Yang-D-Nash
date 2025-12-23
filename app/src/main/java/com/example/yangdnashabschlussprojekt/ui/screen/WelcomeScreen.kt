package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 120.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            AnimatedVisibility(
                visible = visible.value,
                enter = fadeIn(tween(1200)) + scaleIn(initialScale = 0.9f)
            ) {
                WelcomeImage()
            }
            Spacer(modifier = Modifier.height(32.dp))
            AnimatedVisibility(
                visible = visible.value,
                enter = slideInVertically { 40 } + fadeIn(tween(800, 300))
            ) {
                WelcomeGreeting(displayName)
            }
            Spacer(modifier = Modifier.weight(1f))
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