package com.example.yangdnashabschlussprojekt.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.yangdnashabschlussprojekt.ui.screen.ARScreen
import com.example.yangdnashabschlussprojekt.ui.screen.RegistrationScreen
import com.example.yangdnashabschlussprojekt.ui.screen.SettingsScreen
import com.example.yangdnashabschlussprojekt.ui.screen.TextScreen
import com.example.yangdnashabschlussprojekt.ui.screen.WelcomeScreen
import com.example.yangdnashabschlussprojekt.util.ObjectDetectionAnalyzer
import com.google.mlkit.vision.objects.DetectedObject
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController) {

    var topBarTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            if (topBarTitle.isNotEmpty()) {
                TopAppBar(title = { Text(topBarTitle) })
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = WelcomeRoute.route
        ) {

            composable(WelcomeRoute.route) {
                WelcomeScreen(
                    viewModel = koinViewModel(),
                    onOpenSettings = { navController.navigate(SettingsRoute.route) }
                )
            }

            composable(ARScreenRoute.route) {
                val detectedObjects = remember { mutableStateListOf<DetectedObject>() }

                val analyzer = remember {
                    ObjectDetectionAnalyzer(object : ObjectDetectionAnalyzer.DetectionListener {
                        override fun onObjectsDetected(objects: List<DetectedObject>) {
                            detectedObjects.clear()
                            detectedObjects.addAll(objects)
                        }
                    })
                }

                ARScreen(
                )
            }


            composable(SettingsRoute.route) {
                SettingsScreen(
                    onNavigateToRegister = { navController.navigate(RegisterRoute.route) },
                    navController = navController,
                    settingsViewModel = koinViewModel(),
                )
            }

            composable(TextScreenRoute.route) {
                TextScreen(
                    viewModel = koinViewModel(),
                )
            }
            composable(RegisterRoute.route) {
                RegistrationScreen(
                    viewModel = koinViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
