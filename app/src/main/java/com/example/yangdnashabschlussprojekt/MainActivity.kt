package com.example.yangdnashabschlussprojekt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.yangdnashabschlussprojekt.data.remote.repository.VisionRepository
import com.example.yangdnashabschlussprojekt.ui.navigation.AppNavHost
import com.example.yangdnashabschlussprojekt.ui.theme.SmartVisionTheme
import org.koin.android.ext.android.get  // <-- WICHTIGER IMPORT

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartVisionTheme {

                val visionRepository: VisionRepository = get()

                val navController = rememberNavController()

                AppNavHost(
                    navController = navController,
                    visionRepository = visionRepository
                )
            }
        }
    }
}
