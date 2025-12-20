package com.example.yangdnashabschlussprojekt.di

import com.example.yangdnashabschlussprojekt.ui.viewmodel.CameraXManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import org.koin.dsl.module
import java.util.concurrent.Executor
import androidx.core.content.ContextCompat
import org.koin.android.ext.koin.androidContext

val platformModule = module {
    single<Executor> {
        ContextCompat.getMainExecutor(androidContext())
    }
    single<CameraManager> {
        CameraXManager(
            context = androidContext(),
            executor = get()
        )
    }
}