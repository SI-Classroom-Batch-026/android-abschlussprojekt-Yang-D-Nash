package com.example.yangdnashabschlussprojekt.ui.component.text

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun rememberTextToSpeech(): (String) -> Unit {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    val currentLocale = Locale.getDefault()

    DisposableEffect(Unit) {
        val speechListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(currentLocale)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.US
                }
            }
        }
        tts = TextToSpeech(context, speechListener)

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    return { text ->
        if (text.isNotBlank()) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }
}