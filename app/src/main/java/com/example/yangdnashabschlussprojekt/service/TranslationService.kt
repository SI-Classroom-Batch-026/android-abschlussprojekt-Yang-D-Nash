package com.example.yangdnashabschlussprojekt.service

import android.util.Log
import com.example.yangdnashabschlussprojekt.data.remote.model.vision.TranslatedTextResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TranslationService {

    private val tag = "TranslationService"


    suspend fun translateImage(base64ImageString: String): TranslatedTextResult = withContext(Dispatchers.IO) {

        Log.d(tag, "Starting image translation request...")

        kotlinx.coroutines.delay(2500)

        val simulatedOriginalText = "The quick brown fox jumps over the lazy dog."
        val simulatedTranslatedText = "Der schnelle braune Fuchs springt über den faulen Hund."
        
        Log.d(tag, "Translation complete. Original: '$simulatedOriginalText', Translated: '$simulatedTranslatedText'")

        return@withContext TranslatedTextResult(
            originalText = simulatedOriginalText,
            translatedText = simulatedTranslatedText
        )
    }
}