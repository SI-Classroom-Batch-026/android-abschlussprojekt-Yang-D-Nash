package com.example.yangdnashabschlussprojekt.ui.camera

import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.CameraManager
import com.example.yangdnashabschlussprojekt.ui.viewmodel.camera.ImportedImageAsset
import java.awt.FileDialog
import java.awt.Frame
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Base64

class DesktopCameraManager : CameraManager {
    override val platformName: String = "Desktop"
    override val supportsImageImport: Boolean = true

    override fun openCamera(): String {
        val osName = System.getProperty("os.name").lowercase()
        val command = when {
            "mac" in osName -> listOf("open", "-a", "Photo Booth")
            "win" in osName -> listOf("cmd", "/c", "start", "microsoft.windows.camera:")
            else -> listOf(
                "sh",
                "-lc",
                "if command -v cheese >/dev/null 2>&1; then cheese; " +
                    "elif command -v guvcview >/dev/null 2>&1; then guvcview; " +
                    "elif command -v kamera >/dev/null 2>&1; then kamera; " +
                    "else exit 1; fi"
            )
        }

        return runCatching {
            ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()
            "Desktop-Kamera-App wurde gestartet."
        }.getOrElse {
            "Keine Desktop-Kamera-App gefunden. Die Desktop-Kamera braucht noch eine direkte Integration."
        }
    }

    override suspend fun importImage(): ImportedImageAsset? {
        val dialog = FileDialog(null as Frame?, "Bild auswaehlen", FileDialog.LOAD).apply {
            isVisible = true
        }
        val fileName = dialog.file ?: return null
        val directory = dialog.directory ?: return null
        val absolutePath = Paths.get(directory, fileName).toAbsolutePath().toString()
        val base64Content = runCatching {
            Base64.getEncoder().encodeToString(Files.readAllBytes(Paths.get(absolutePath)))
        }.getOrNull() ?: return null
        return ImportedImageAsset(
            fileName = fileName,
            absolutePath = absolutePath,
            base64Content = base64Content
        )
    }
}
