package com.dev.unlost.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

sealed class SpeechState {
    object Idle : SpeechState()
    data class Listening(val remainingSeconds: Int? = null) : SpeechState()
    data class PartialResult(val result: String, val remainingSeconds: Int? = null) : SpeechState()
    data class Success(val result: String) : SpeechState()
    data class Error(val message: String) : SpeechState()
}

@Singleton
class SpeechRecognizerHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun startListening(autoStopEnabled: Boolean = true, timeoutSeconds: Int = 5): Flow<SpeechState> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(SpeechState.Error("Tính năng nhận diện giọng nói không hỗ trợ trên thiết bị này"))
            close()
            return@callbackFlow
        }

        val session = SpeechSession(
            context = context,
            timeoutMs = timeoutSeconds.coerceIn(2, 10) * 1000L,
            autoStopEnabled = autoStopEnabled,
            scope = this
        )

        session.start()

        awaitClose {
            session.cleanup()
        }
    }
}

private class SpeechSession(
    private val context: Context,
    private val timeoutMs: Long,
    private val autoStopEnabled: Boolean,
    private val scope: ProducerScope<SpeechState>
) {
    private var currentRecognizer: SpeechRecognizer? = null
    private var lastActivityTime = System.currentTimeMillis()
    private val accumulatedText = StringBuilder()
    private var latestPartialText: String = ""
    private var silenceJob: Job? = null
    private var isSessionActive = true
    private var isUserSpeaking = false
    private var hasSpeechStarted = false
    private var hasInitialReadyHappened = false
    private var lastEmittedSec: Int? = null

    fun start() {
        resetSilenceCheck()
        startSubSession()
    }

    fun cleanup() {
        isSessionActive = false
        silenceJob?.cancel()
        destroyCurrentRecognizer()
    }

    private fun startSubSession() {
        if (!isSessionActive) return
        scope.launch(Dispatchers.Main) {
            try {
                destroyCurrentRecognizer()
                val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                currentRecognizer = recognizer
                recognizer.setRecognitionListener(createListener())
                recognizer.startListening(createIntent())
            } catch (_: Exception) {
            }
        }
    }

    private fun restartListeningSubSession() {
        if (!isSessionActive) return
        scope.launch(Dispatchers.Main) {
            try {
                if (currentRecognizer == null) {
                    startSubSession()
                } else {
                    currentRecognizer?.startListening(createIntent())
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
        if (result.isNotEmpty()) {
            scope.trySend(SpeechState.Success(result))
        } else {
            scope.trySend(SpeechState.Error("Không nghe thấy giọng nói"))
        }
    }

    fun markSpeechActive() {
        hasSpeechStarted = true
        isUserSpeaking = true
        lastActivityTime = System.currentTimeMillis()
    }

    fun markSpeechPaused() {
        isUserSpeaking = false
        lastActivityTime = System.currentTimeMillis()
    }

    fun resetSilenceCheck() {
        silenceJob?.cancel()
        if (autoStopEnabled) {
            silenceJob = scope.launch(Dispatchers.Main) {
                while (isSessionActive) {
                    val currentTimeout = if (hasSpeechStarted || getBestText().isNotEmpty()) timeoutMs else INITIAL_WAIT_TIMEOUT_MS
                    val elapsed = System.currentTimeMillis() - lastActivityTime
                    val remainingMs = currentTimeout - elapsed

                    if (remainingMs <= 0) {
                        finalizeSuccess()
                        break
                    }

                    val remainingSec = if (hasSpeechStarted || getBestText().isNotEmpty()) {
                        ((remainingMs + 999L) / 1000L).toInt()
                    } else {
                        ((remainingMs + 999L) / 1000L).coerceAtMost(timeoutMs / 1000L).toInt()
                    }
                    if (remainingSec != lastEmittedSec) {
                        lastEmittedSec = remainingSec
                        scope.trySend(SpeechState.Listening(remainingSec))
                    }
                    delay(50L)
                }
            }
        } else {
            scope.trySend(SpeechState.Listening(null))
        }
    }

    private fun checkSilenceAndContinue() {
        if (!isSessionActive) return
        val currentTimeout = if (hasSpeechStarted || getBestText().isNotEmpty()) timeoutMs else INITIAL_WAIT_TIMEOUT_MS
        val elapsed = System.currentTimeMillis() - lastActivityTime
        if (autoStopEnabled && elapsed >= currentTimeout) {
            finalizeSuccess()
        } else {
            restartListeningSubSession()
        }
    }

    fun handleResults(results: Bundle?) {
        if (!isSessionActive) return
        markSpeechPaused()
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            appendRecognizedText(matches[0])
            latestPartialText = ""
            val sec = lastEmittedSec ?: (timeoutMs / 1000L).toInt()
            scope.trySend(SpeechState.PartialResult(getBestText(), sec))
        }
        checkSilenceAndContinue()
    }

    fun handlePartialResults(partialResults: Bundle?) {
        if (!isSessionActive) return
        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (!matches.isNullOrEmpty()) {
            markSpeechActive()
            latestPartialText = matches[0].trim()
            val sec = lastEmittedSec ?: (timeoutMs / 1000L).toInt()
            scope.trySend(SpeechState.PartialResult(getBestText(), sec))
        }
    }

    fun handleError(error: Int) {
        if (!isSessionActive) return
        markSpeechPaused()
        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
            checkSilenceAndContinue()
        } else {
            val currentTimeout = if (hasSpeechStarted || getBestText().isNotEmpty()) timeoutMs else INITIAL_WAIT_TIMEOUT_MS
            val elapsed = System.currentTimeMillis() - lastActivityTime
            if (getBestText().isNotEmpty() || elapsed >= currentTimeout) {
                finalizeSuccess()
            } else {
                checkSilenceAndContinue()
            }
        }
    }

    private fun createListener(): RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            if (!hasInitialReadyHappened) {
                hasInitialReadyHappened = true
                lastActivityTime = System.currentTimeMillis()
            }
            scope.trySend(SpeechState.Listening(lastEmittedSec ?: (timeoutMs / 1000L).toInt()))
        }
        override fun onBeginningOfSpeech() { markSpeechActive() }
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() { markSpeechPaused() }
        override fun onError(error: Int) { handleError(error) }
        override fun onResults(results: Bundle?) { handleResults(results) }
        override fun onPartialResults(partialResults: Bundle?) { handlePartialResults(partialResults) }
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun createIntent(): Intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "vi-VN")
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
    }

    private fun appendRecognizedText(newText: String) {
        val trimmed = newText.trim()
        if (trimmed.isEmpty()) return
        if (accumulatedText.isNotEmpty() && !accumulatedText.endsWith(" ")) {
            accumulatedText.append(" ")
        }
        accumulatedText.append(trimmed)
    }

    private fun mapErrorToMessage(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_AUDIO -> "Lỗi ghi âm"
        SpeechRecognizer.ERROR_NO_MATCH -> "Không nghe thấy giọng nói"
        SpeechRecognizer.ERROR_NETWORK -> "Lỗi kết nối mạng"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Đã hết thời gian chờ giọng nói"
        else -> "Lỗi nhận diện giọng nói ($error)"
    }

    companion object {
        private const val INITIAL_WAIT_TIMEOUT_MS = 6000L
    }
}
