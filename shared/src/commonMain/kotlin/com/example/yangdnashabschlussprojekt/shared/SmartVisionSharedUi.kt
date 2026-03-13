package com.example.yangdnashabschlussprojekt.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SmartVisionScreenBackground(modifier: Modifier = Modifier): Modifier {
    val colorScheme = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    return modifier
        .fillMaxSize()
        .background(
            Brush.verticalGradient(
                colors = if (isDark) {
                    listOf(
                        colorScheme.primary.copy(alpha = 0.15f),
                        colorScheme.background
                    )
                } else {
                    listOf(
                        colorScheme.primary.copy(alpha = 0.18f),
                        colorScheme.background
                    )
                }
            )
        )
}

@Composable
fun SmartVisionLiveScreenBackground(modifier: Modifier = Modifier): Modifier {
    return modifier
        .fillMaxSize()
        .background(Color.Black)
        .background(
            Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.5f)
                ),
                radius = 900f
            )
        )
}

@Composable
fun SmartVisionGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.58f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    ) {
        content()
    }
}

@Composable
fun SmartVisionAccentCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        content()
    }
}

@Composable
fun SmartVisionStatusCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.14f)
    ) {
        content()
    }
}

@Composable
fun SmartVisionHeader(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .systemBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.size(width = 92.dp, height = 40.dp)
            ) {
                Text("Zurueck", color = MaterialTheme.colorScheme.onBackground)
            }
        } else {
            Box(modifier = Modifier.size(width = 92.dp, height = 40.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
        if (trailing != null) {
            Box(
                modifier = Modifier.size(width = 92.dp, height = 40.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                trailing()
            }
        } else {
            Box(modifier = Modifier.size(width = 92.dp, height = 40.dp))
        }
    }
}
