package com.example.yangdnashabschlussprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.navigation.compose.rememberNavController
import com.example.yangdnashabschlussprojekt.ui.navigation.AppNavHost
import com.example.yangdnashabschlussprojekt.ui.theme.DeepSpaceBlack
import com.example.yangdnashabschlussprojekt.ui.theme.DeepSpaceCyan
import com.example.yangdnashabschlussprojekt.ui.theme.LightBgEnd
import com.example.yangdnashabschlussprojekt.ui.theme.LightBgStart
import com.example.yangdnashabschlussprojekt.ui.theme.SmartVisionTheme
import android.graphics.Color as AndroidColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            SmartVisionTheme {
                val isDark = isSystemInDarkTheme()
                Box(modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(
                        if (isDark) listOf(DeepSpaceCyan, DeepSpaceBlack)
                        else listOf(LightBgStart, LightBgEnd)
                    )
                )) {
                    AppNavHost(navController = rememberNavController())
                }
            }
        }
    }
}