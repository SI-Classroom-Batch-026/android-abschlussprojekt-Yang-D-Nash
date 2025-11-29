package com.example.yangdnashabschlussprojekt.util

import android.graphics.Rect

fun scaleRect(rect: Rect, bitmapWidth: Int, bitmapHeight: Int, viewWidth: Int, viewHeight: Int): Rect {
    val scaleX = viewWidth.toFloat() / bitmapWidth
    val scaleY = viewHeight.toFloat() / bitmapHeight
    return Rect(
        (rect.left * scaleX).toInt(),
        (rect.top * scaleY).toInt(),
        (rect.right * scaleX).toInt(),
        (rect.bottom * scaleY).toInt()
    )
}