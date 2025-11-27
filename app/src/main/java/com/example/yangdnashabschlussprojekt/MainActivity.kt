package com.example.yangdnashabschlussprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.yangdnashabschlussprojekt.ui.navigation.AppNavHost
import com.example.yangdnashabschlussprojekt.ui.theme.SmartVisionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartVisionTheme {
                val navController = rememberNavController()
                AppNavHost(navController)
            }
        }
    }
}