package com.example.yangdnashabschlussprojekt.util.notification // Dein Package-Pfad anpassen

import com.example.yangdnashabschlussprojekt.R
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
object TranslatorUtil {
    fun translateDynamic(
        context: android.content.Context,
        sourceText: String,
        targetLang: String,
        onStatusUpdate: (String) -> Unit,
        onResult: (String) -> Unit
    ) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLang) ?: TranslateLanguage.GERMAN)
            .build()

        val translator = Translation.getClient(options)
        val conditions = DownloadConditions.Builder().build()

        onStatusUpdate(context.getString(R.string.msg_downloading_language))

        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                onStatusUpdate(context.getString(R.string.msg_translating))

                translator.translate(sourceText)
                    .addOnSuccessListener { translatedText ->
                        onResult(translatedText)
                        translator.close()
                    }
                    .addOnFailureListener {
                        onResult(sourceText)
                        translator.close()
                    }
            }
            .addOnFailureListener {
                onStatusUpdate(context.getString(R.string.err_translation_unavailable))
                onResult(sourceText)
            }
    }
}