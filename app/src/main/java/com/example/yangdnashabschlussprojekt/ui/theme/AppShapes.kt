package com.example.yangdnashabschlussprojekt.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

val AppShapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(32.dp), // Für ModalSheets und Haupt-Karten
    extraLarge = RoundedCornerShape(40.dp) // Für Buttons
)