package com.example.yangdnashabschlussprojekt.ui.component.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

fun ImageProxy.myBitmap(): Bitmap {
    val yPlane = planes[0]
    val uPlane = planes[1]
    val vPlane = planes[2]

    val ySize = yPlane.buffer.remaining()
    val uSize = uPlane.buffer.remaining()
    val vSize = vPlane.buffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copy Y
    yPlane.buffer.get(nv21, 0, ySize)

    // Copy VU (NV21 requires V then U)
    var uvIndex = ySize
    for (i in 0 until vSize) nv21[uvIndex++] = vPlane.buffer.get(i)
    for (i in 0 until uSize) nv21[uvIndex++] = uPlane.buffer.get(i)

    // Convert NV21 to Bitmap directly
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val bytes = out.toByteArray()

    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?.copy(Bitmap.Config.ARGB_8888, true) ?: throw IllegalStateException("Bitmap decode failed")

    // Apply rotation
    val rotation = imageInfo.rotationDegrees
    return if (rotation != 0) {
        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}
