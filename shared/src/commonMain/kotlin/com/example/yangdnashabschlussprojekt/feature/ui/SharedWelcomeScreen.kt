package com.example.yangdnashabschlussprojekt.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yangdnashabschlussprojekt.shared.SmartVisionAccentCard
import com.example.yangdnashabschlussprojekt.shared.SmartVisionGlassCard
import com.example.yangdnashabschlussprojekt.shared.SmartVisionScreenBackground

@Composable
fun SharedWelcomeScreen(
    displayName: String,
    onRestartOnboarding: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current

    Box(
        modifier = modifier
            .then(SmartVisionScreenBackground())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 24.dp + contentPadding.calculateStartPadding(layoutDirection),
                    end = 24.dp + contentPadding.calculateEndPadding(layoutDirection),
                    top = contentPadding.calculateTopPadding() + 24.dp,
                    bottom = contentPadding.calculateBottomPadding() + 24.dp
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SmartVisionGlassCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SMARTVISION",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelSmall,
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Hallo, $displayName",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SmartVision verbindet Live-Objekterkennung, Textscan, Uebersetzung und Verlauf zu einer mobilen Assistenz-App.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SmartVisionAccentCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onRestartOnboarding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Onboarding erneut starten",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
