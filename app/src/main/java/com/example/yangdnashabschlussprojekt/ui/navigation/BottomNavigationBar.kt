package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.yangdnashabschlussprojekt.ui.theme.LightOnPrimary
import com.example.yangdnashabschlussprojekt.ui.theme.LightPrimary

val items = listOf(
    BottomNavItem(WelcomeRoute, Icons.Default.Home),
    BottomNavItem(SettingsRoute, Icons.Default.Settings),
    BottomNavItem(ARScreenRoute, Icons.Default.Info),
    BottomNavItem(TextScreenRoute, Icons.Default.AccessibilityNew)
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(
        containerColor = LightPrimary,
        contentColor = LightOnPrimary
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.routeObj.label) },
                label = { Text(item.routeObj.label) },
                selected = currentRoute == item.routeObj.route,
                onClick = {
                    navController.navigate(item.routeObj.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}


