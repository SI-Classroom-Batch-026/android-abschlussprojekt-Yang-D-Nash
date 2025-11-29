package com.example.yangdnashabschlussprojekt.util.image

import android.content.Context
import android.widget.Toast
import java.io.File

fun saveTextAsFile(context: Context, text: String) {
    try {
        if (text.isBlank()) {
            Toast.makeText(context, "Kein Text zum Speichern", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "ocr-${System.currentTimeMillis()}.txt"
        val file = File(context.getExternalFilesDir(null), fileName)
        file.writeText(text)
        Toast.makeText(context, "Text gespeichert: ${file.absolutePath}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Fehler beim Speichern: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}
