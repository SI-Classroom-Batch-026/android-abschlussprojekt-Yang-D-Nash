package com.example.yangdnashabschlussprojekt.ui.camera

import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager

class AndroidCameraManager : CameraManager {
    override fun openCamera() {
        println("Android Kamera mit ML Kit startet...")
    }
}