package com.example.yangdnashabschlussprojekt.feature.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

internal data class AndroidCameraPermissionState(
    val isGranted: Boolean,
    val hasBeenDenied: Boolean
)

@Composable
internal fun rememberAndroidCameraPermissionState(): AndroidCameraPermissionState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isGranted by remember { mutableStateOf(context.hasCameraPermission()) }
    var hasRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        hasRequested = true
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isGranted = context.hasCameraPermission()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(isGranted, hasRequested) {
        if (!isGranted && !hasRequested) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    return AndroidCameraPermissionState(
        isGranted = isGranted,
        hasBeenDenied = !isGranted && hasRequested
    )
}

internal fun Context.hasCameraPermission(): Boolean {
    return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED
}
