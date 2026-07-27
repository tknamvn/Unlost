package com.dev.unlost.util

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent

object SpeechIntentFactory {
    fun isEmulator(): Boolean {
        val build = android.os.Build.FINGERPRINT
        val model = android.os.Build.MODEL
        val brand = android.os.Build.BRAND
        val device = android.os.Build.DEVICE
        val hardware = android.os.Build.HARDWARE
        val product = android.os.Build.PRODUCT
        val manufacturer = android.os.Build.MANUFACTURER

        return (brand.startsWith("generic") && device.startsWith("generic"))
                || build.startsWith("generic")
                || build.startsWith("unknown")
                || hardware.contains("goldfish")
                || hardware.contains("ranchu")
                || model.contains("google_sdk")
                || model.contains("Emulator")
                || model.contains("Android SDK built for x86")
                || manufacturer.contains("Genymotion")
                || product.contains("sdk")
                || product.contains("google_sdk")
    }

    fun createRecognizeIntent(context: Context): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            if (!isEmulator()) {
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            }
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra("android.speech.extra.DICTATION_MODE", true)
            putExtra("android.speech.extra.PARTIAL_RESULTS_STREAMING", true)
            putExtra("android.speech.extra.UNSTABLE_TEXT", true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 1000L)
            putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_LENGTH_MILLIS", 1L)
            putExtra("android.speech.extras.SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS", 1000L)
            putExtra("android.speech.extras.SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS", 1000L)
        }
    }
}
