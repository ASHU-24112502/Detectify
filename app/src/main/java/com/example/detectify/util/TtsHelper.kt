package com.example.detectify.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class TtsHelper(private val context: Context) {
    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                ready = true
            }
        }
    }

    fun speak(text: String) {
        if (!ready) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "DetectifyUtter")
    }

    fun shutdown() {
        tts?.shutdown()
        tts = null
    }
}
