package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: Any,      // Jetzt Any statt NavRoute, da es Objekte sind
    val icon: ImageVector,
    val label: String    // Label direkt hier, da das Objekt selbst keinen String mehr hat
)