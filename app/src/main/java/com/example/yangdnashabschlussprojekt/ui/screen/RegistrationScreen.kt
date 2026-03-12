package com.example.yangdnashabschlussprojekt.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.example.yangdnashabschlussprojekt.feature.ui.SharedRegistrationRoute

@Composable
fun RegistrationScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    SharedRegistrationRoute(onBack = onBack)
}
