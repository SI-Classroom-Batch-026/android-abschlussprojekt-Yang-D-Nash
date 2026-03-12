package com.example.yangdnashabschlussprojekt.ui.camera

import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager

class AndroidCameraManager : CameraManager {
    override val platformName: String = "Android"

    override fun openCamera(): String {
        println("Android shared camera bridge invoked.")
        return "Android-Kamera-Bridge ist fur die native CameraX-UI bereit."
    }
}
