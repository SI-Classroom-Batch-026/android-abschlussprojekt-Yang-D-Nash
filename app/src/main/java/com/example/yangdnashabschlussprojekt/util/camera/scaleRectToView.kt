package com.example.yangdnashabschlussprojekt.util.camera

import android.graphics.Rect

/**
 * Skaliert ein Rechteck aus Bitmap-Koordinaten auf View-Koordinaten
 * Berücksichtigt dabei Portrait-Kamera (bitmapHeight <-> bitmapWidth getauscht)
 */
fun scaleRectToView(rect: Rect, bitmapHeight: Int, bitmapWidth: Int, viewWidth: Int, viewHeight: Int): Rect {
    // Verhältnis von View zu Bitmap
    val scaleX = viewWidth.toFloat() / bitmapWidth
    val scaleY = viewHeight.toFloat() / bitmapHeight

    // Letterboxing / centering falls nötig
    val scaledLeft = rect.left * scaleX
    val scaledTop = rect.top * scaleY
    val scaledRight = rect.right * scaleX
    val scaledBottom = rect.bottom * scaleY

    return Rect(
        scaledLeft.toInt(),
        scaledTop.toInt(),
        scaledRight.toInt(),
        scaledBottom.toInt()
    )
}
