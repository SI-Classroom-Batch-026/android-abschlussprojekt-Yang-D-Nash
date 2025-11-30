package com.example.yangdnashabschlussprojekt.util.camera

import android.graphics.Rect

/**
 * Rotates a Rect from the original bitmap coordinates according to the rotationDegrees.
 * Assumes rotationDegrees is 0, 90, 180, 270.
 */
fun rotateRect(rect: Rect, rotationDegrees: Int, bitmapWidth: Int, bitmapHeight: Int): Rect {
    return when (rotationDegrees % 360) {
        0 -> Rect(rect)
        90 -> Rect(
            rect.top,
            bitmapWidth - rect.right,
            rect.bottom,
            bitmapWidth - rect.left
        )
        180 -> Rect(
            bitmapWidth - rect.right,
            bitmapHeight - rect.bottom,
            bitmapWidth - rect.left,
            bitmapHeight - rect.top
        )
        270 -> Rect(
            bitmapHeight - rect.bottom,
            rect.left,
            bitmapHeight - rect.top,
            rect.right
        )
        else -> Rect(rect)
    }
}

/**
 * Scales a Rect from bitmap coordinates to the view coordinates, preserving aspect ratio.
 * Letterboxing is handled automatically.
 */

