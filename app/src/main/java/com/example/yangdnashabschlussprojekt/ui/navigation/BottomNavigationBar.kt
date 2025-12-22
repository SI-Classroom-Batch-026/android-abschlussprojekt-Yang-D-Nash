package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(WelcomeRoute, Icons.Default.Home, "Home"),
        BottomNavItem(SettingsRoute, Icons.Default.Settings, "Settings"),
        BottomNavItem(ARScreenRoute, Icons.Default.Info, "AR"),
        BottomNavItem(TextScreenRoute, Icons.Default.AccessibilityNew, "Text")
    )

    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .navigationBarsPadding()
            .clip(RoundedCornerShape(28.dp)),
        color = Color(0xFF1C1B1F).copy(alpha = 0.85f),
        tonalElevation = 0.dp,
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.height(64.dp),
            windowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            items.forEach { item ->
                // Type-Safe Check ob die Route aktiv ist
                val isSelected = currentDestination?.hasRoute(item.route::class) ?: false

                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        // FIX: Wir navigieren nur, wenn wir nicht schon dort sind
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                // WICHTIG: Wir poppen zum Root-Objekt (WelcomeRoute)
                                // statt zur abstrakten ID
                                popUpTo<WelcomeRoute> {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color.White.copy(alpha = 0.1f),
                        unselectedIconColor = Color.White.copy(alpha = 0.5f),
                        unselectedTextColor = Color.White.copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}