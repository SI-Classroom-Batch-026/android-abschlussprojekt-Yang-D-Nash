package com.example.yangdnashabschlussprojekt.desktop

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.yangdnashabschlussprojekt.di.desktopModule
import com.example.yangdnashabschlussprojekt.di.sharedModul.commonModule
import com.example.yangdnashabschlussprojekt.shared.App
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(commonModule, desktopModule)
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SmartVision Desktop"
        ) {
            App()
        }
    }
}
