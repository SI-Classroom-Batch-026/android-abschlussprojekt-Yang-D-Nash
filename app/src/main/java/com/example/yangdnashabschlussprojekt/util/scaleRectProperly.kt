package com.example.yangdnashabschlussprojekt.util

import android.graphics.Rect
import kotlin.math.max

fun scaleRectProperly(
    imageRect: Rect,
    imageWidth: Int,
    imageHeight: Int,
    viewWidth: Int,
    viewHeight: Int
): Rect {
    if (imageWidth == 0 || imageHeight == 0) return Rect(0,0,0,0)

    val scale = max(viewWidth.toFloat() / imageWidth.toFloat(), viewHeight.toFloat() / imageHeight.toFloat())
    val scaledImageWidth = imageWidth * scale
    val scaledImageHeight = imageHeight * scale

    val offsetX = (viewWidth - scaledImageWidth) / 2f
    val offsetY = (viewHeight - scaledImageHeight) / 2f

    val left = imageRect.left * scale + offsetX
    val top = imageRect.top * scale + offsetY
    val right = imageRect.right * scale + offsetX
    val bottom = imageRect.bottom * scale + offsetY

    return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}
