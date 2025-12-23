package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
                        if (!isSelected) {
                            navController.navigate(item.route) {
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