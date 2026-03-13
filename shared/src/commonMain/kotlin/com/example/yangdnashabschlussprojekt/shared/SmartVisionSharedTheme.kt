package com.example.yangdnashabschlussprojekt.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

private val SharedDarkColors = darkColorScheme(
    primary = Color(0xFF00F2FF),
    onPrimary = Color.Black,
    secondary = Color(0xFFFF00FF),
    onSecondary = Color.White,
    tertiary = Color(0xFF7000FF),
    background = Color(0xFF030507),
    surface = Color(0xFF0E121A),
    surfaceVariant = Color(0xFF121722),
    onBackground = Color(0xFFF0F0F0),
    onSurface = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFFB6BDC8),
    outline = Color(0xFF2C2C2E)
)

private val SharedLightColors = lightColorScheme(
    primary = Color(0xFF0061A4),
    onPrimary = Color.White,
    secondary = Color(0xFF0061A4),
    onSecondary = Color.White,
    background = Color(0xFFF8F9FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8EEF7),
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color(0xFF556070)
)

private val SharedTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.5.sp
    )
)

private val SharedShapes = Shapes(
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(32.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(40.dp)
)

@Composable
fun SmartVisionSharedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) SharedDarkColors else SharedLightColors,
        typography = SharedTypography,
        shapes = SharedShapes,
        content = content
    )
}
