package com.example.yangdnashabschlussprojekt.util.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.mlkit.vision.text.Text

fun markTextBlocks(bitmap: Bitmap, textBlocks: List<Text.TextBlock>): Bitmap {
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)
    val paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 5f
    }

    for (block in textBlocks) {
        block.boundingBox?.let { canvas.drawRect(it, paint) }
    }

    return mutableBitmap
}
