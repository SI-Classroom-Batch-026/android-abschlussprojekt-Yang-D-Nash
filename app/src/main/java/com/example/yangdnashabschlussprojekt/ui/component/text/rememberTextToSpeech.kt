package com.example.yangdnashabschlussprojekt.ui.component.text

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
@Composable
fun rememberTextToSpeech(): (String) -> Unit {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        val speechListener = TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.GERMAN
            }
        }
        tts = TextToSpeech(context, speechListener)

        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    return { text ->
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}