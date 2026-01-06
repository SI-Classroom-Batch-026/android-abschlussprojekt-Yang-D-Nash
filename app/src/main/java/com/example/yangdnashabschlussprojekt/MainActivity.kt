package com.example.yangdnashabschlussprojekt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.yangdnashabschlussprojekt.ui.navigation.AppNavHost
import com.example.yangdnashabschlussprojekt.ui.theme.DeepSpaceBlack
import com.example.yangdnashabschlussprojekt.ui.theme.DeepSpaceCyan
import com.example.yangdnashabschlussprojekt.ui.theme.LightBgEnd
import com.example.yangdnashabschlussprojekt.ui.theme.LightBgStart
import com.example.yangdnashabschlussprojekt.ui.theme.SmartVisionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SmartVisionTheme {
                val context = LocalContext.current
                val isDark = isSystemInDarkTheme()
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (!isGranted) {
                        Toast.makeText(context, "Kamera wird für AR benötigt", Toast.LENGTH_SHORT).show()
                    }
                }
                LaunchedEffect(Unit) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            if (isDark) listOf(DeepSpaceCyan, DeepSpaceBlack)
                            else listOf(LightBgStart, LightBgEnd)
                        )
                    )
                ) {
                    AppNavHost(navController = rememberNavController())
                }
            }
        }
    }
}