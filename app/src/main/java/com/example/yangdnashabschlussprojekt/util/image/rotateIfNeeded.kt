package com.example.yangdnashabschlussprojekt.util.image

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.rotateIfNeeded(rotationDegrees: Int): Bitmap {
    if (rotationDegrees == 0) return this
    val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
