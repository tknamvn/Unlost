package com.dev.unlost.util

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SpeechSession(
    private val context: Context,
    private val timeoutMs: Long,
    private val autoStopEnabled: Boolean,
    private val scope: ProducerScope<SpeechState>,
    private var prewarmedRecognizer: SpeechRecognizer? = null
) {
    private var currentRecognizer: SpeechRecognizer? = null
    private var lastSilenceStartTime = System.currentTimeMillis()
    private val accumulatedText = StringBuilder()
    private var latestPartialText: String = ""
    private var silenceJob: Job? = null
    private var isSessionActive = true
    private var lastEmittedSec: Int? = null

    fun start() {
        lastSilenceStartTime = System.currentTimeMillis()
        val initialSec = (timeoutMs / 1000L).toInt()
        lastEmittedSec = initialSec
        scope.trySend(SpeechState.Listening(initialSec))
        startSilenceTimer()
        startSubSession()
    }

    fun cleanup() {
        isSessionActive = false
        silenceJob?.cancel()
        destroyCurrentRecognizer()
    }

    private fun startSilenceTimer() {
        silenceJob?.cancel()
        if (!autoStopEnabled) return

        silenceJob = scope.launch(Dispatchers.Main.immediate) {
            while (isSessionActive) {
                val now = System.currentTimeMillis()
                val remainingMs = timeoutMs - (now - lastSilenceStartTime)
                if (remainingMs <= 0) {
                    finalizeSuccess()
                    break
                }
                val remainingSec = ((remainingMs + 999L) / 1000L).toInt().coerceIn(1, (timeoutMs / 1000L).toInt())
                if (remainingSec != lastEmittedSec) {
                    lastEmittedSec = remainingSec
                    emitCurrentState(remainingSec)
                }
                delay(30L)
            }
        }
    }

    private fun emitCurrentState(remainingSec: Int?) {
        val currentText = getBestText()
        if (currentText.isNotEmpty()) {
            scope.trySend(SpeechState.PartialResult(currentText, remainingSec))
        } else {
            scope.trySend(SpeechState.Listening(remainingSec))
        }
    }

    private fun startSubSession() {
        if (!isSessionActive) return
        scope.launch(Dispatchers.Main.immediate) {
            try {
                destroyCurrentRecognizer()
                val recognizer = prewarmedRecognizer ?: SpeechRecognizer.createSpeechRecognizer(context)
                prewarmedRecognizer = null
                currentRecognizer = recognizer
                recognizer.setRecognitionListener(createListener())
                recognizer.startListening(SpeechIntentFactory.createRecognizeIntent(context))
            } catch (_: Exception) {}
        }
    }

    private fun restartSubSession() {
        if (!isSessionActive) return
        scope.launch(Dispatchers.Main.immediate) {
            if (!isSessionActive) return@launch
            try {
                if (currentRecognizer != null) {
                    currentRecognizer?.startListening(SpeechIntentFactory.createRecognizeIntent(context))
                } else {
                    startSubSession()
                }
            } catch (_: Exception) {
                startSubSession()
            }
        }
    }

    private fun destroyCurrentRecognizer() {
        try {
            currentRecognizer?.destroy()
        } catch (_: Exception) {}
        currentRecognizer = null
    }

    private fun getBestText(): String {
        if (latestPartialText.isNotBlank()) {
            val temp = StringBuilder(accumulatedText)
            if (temp.isNotEmpty() && !temp.endsWith(" ")) temp.append(" ")
            temp.append(latestPartialText.trim())
            return temp.toString().trim()
        }
        return accumulatedText.toString().trim()
    }

    fun finalizeSuccess() {
        if (!isSessionActive) return
        isSessionActive = false
        silenceJob?.cancel()
        destroyCurrentRecognizer()
        val result = getBestText()
        if (result.isNotEmpty()) scope.trySend(SpeechState.Success(result))
        else scope.trySend(SpeechState.Error("Không nghe thấy giọng nói"))
    }

    private fun isValidSpeechText(text: String): Boolean =
        text.trim().let { it.isNotEmpty() && it.any { c -> c.isLetterOrDigit() } }

    private fun checkSilenceAndContinue() {
        if (!isSessionActive) return
        if (autoStopEnabled && (System.currentTimeMillis() - lastSilenceStartTime) >= timeoutMs) {
            finalizeSuccess()
        } else {
            restartSubSession()
        }
    }

    fun handleResults(results: Bundle?) {
        if (!isSessionActive) return
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty() && isValidSpeechText(matches[0])) {
            appendRecognizedText(matches[0])
            latestPartialText = ""
            emitCurrentState(lastEmittedSec)
        }
        checkSilenceAndContinue()
    }

    fun handlePartialResults(partialResults: Bundle?) {
        if (!isSessionActive) return
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        val unstable = partialResults?.getStringArrayList("android.speech.extra.UNSTABLE_TEXT")
            ?: partialResults?.getCharSequence("android.speech.extra.UNSTABLE_TEXT")?.let { arrayListOf(it.toString()) }
            ?: partialResults?.getStringArrayList("unstable_text")

        val rawCandidate = matches?.firstOrNull()?.trim()?.ifEmpty { null }
            ?: unstable?.firstOrNull()?.toString()?.trim()

        if (!rawCandidate.isNullOrEmpty() && isValidSpeechText(rawCandidate)) {
            latestPartialText = rawCandidate
            lastSilenceStartTime = System.currentTimeMillis()
            val totalTimeoutSec = (timeoutMs / 1000L).toInt()
            lastEmittedSec = totalTimeoutSec
            scope.trySend(SpeechState.PartialResult(getBestText(), totalTimeoutSec))
        }
    }

    fun updateSpeechActivity() {
        lastSilenceStartTime = System.currentTimeMillis()
        val totalTimeoutSec = (timeoutMs / 1000L).toInt()
        lastEmittedSec = totalTimeoutSec
        emitCurrentState(totalTimeoutSec)
    }

    fun handleError(error: Int) {
        if (!isSessionActive) return
        if ((error == SpeechRecognizer.ERROR_AUDIO || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) && getBestText().isEmpty()) {
            isSessionActive = false
            silenceJob?.cancel()
            destroyCurrentRecognizer()
            scope.trySend(SpeechState.Error("Lỗi ghi âm ($error)"))
            return
        }
        checkSilenceAndContinue()
    }

    private fun createListener(): RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() { lastSilenceStartTime = System.currentTimeMillis() }
        override fun onRmsChanged(rmsdB: Float) { if (rmsdB > 2.0f) lastSilenceStartTime = System.currentTimeMillis() }
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) { handleError(error) }
        override fun onResults(results: Bundle?) { handleResults(results) }
        override fun onPartialResults(partialResults: Bundle?) { handlePartialResults(partialResults) }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun appendRecognizedText(newText: String) {
        val trimmed = newText.trim()
        if (trimmed.isEmpty()) return
        if (accumulatedText.isNotEmpty() && !accumulatedText.endsWith(" ")) accumulatedText.append(" ")
        accumulatedText.append(trimmed)
    }
}
