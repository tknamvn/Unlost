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

    private class SpeechSession(
        private val context: Context,
        private val timeoutMs: Long,
        private val autoStopEnabled: Boolean,
        private val scope: ProducerScope<SpeechState>
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
            if (!autoStopEnabled) {
                scope.trySend(SpeechState.Listening(null))
                return
            }

            silenceJob = scope.launch(Dispatchers.Main) {
                while (isSessionActive) {
                    val now = System.currentTimeMillis()
                    val elapsed = now - lastSilenceStartTime
                    val remainingMs = timeoutMs - elapsed

                    if (remainingMs <= 0) {
                        finalizeSuccess()
                        break
                    }

                    val totalTimeoutSec = (timeoutMs / 1000L).toInt()
                    val remainingSec = ((remainingMs + 999L) / 1000L).toInt().coerceIn(1, totalTimeoutSec)

                    if (remainingSec != lastEmittedSec) {
                        lastEmittedSec = remainingSec
                        emitCurrentState(remainingSec)
                    }
                    delay(50L)
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
            scope.launch(Dispatchers.Main) {
                try {
                    destroyCurrentRecognizer()
                    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                    currentRecognizer = recognizer
                    recognizer.setRecognitionListener(createListener())
                    recognizer.startListening(createIntent())
                } catch (_: Exception) {}
            }
        }

        private fun restartSubSession() {
            if (!isSessionActive) return
            scope.launch(Dispatchers.Main) {
                delay(30L)
                if (!isSessionActive) return@launch
                startSubSession()
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

        private fun isValidSpeechText(text: String): Boolean {
            val trimmed = text.trim()
            return trimmed.isNotEmpty() && trimmed.any { it.isLetterOrDigit() }
        }

        fun updateSpeechActivity() {
            lastSilenceStartTime = System.currentTimeMillis()
            val totalTimeoutSec = (timeoutMs / 1000L).toInt()
            lastEmittedSec = totalTimeoutSec
            emitCurrentState(totalTimeoutSec)
        }

        private fun checkSilenceAndContinue() {
            if (!isSessionActive) return
            val elapsed = System.currentTimeMillis() - lastSilenceStartTime
            if (autoStopEnabled && elapsed >= timeoutMs) {
                finalizeSuccess()
            } else {
                restartSubSession()
            }
        }

        fun handleResults(results: Bundle?) {
            if (!isSessionActive) return
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0].trim()
                if (isValidSpeechText(text)) {
                    appendRecognizedText(text)
                    latestPartialText = ""
                    emitCurrentState(lastEmittedSec)
                }
            }
            checkSilenceAndContinue()
        }

        fun handlePartialResults(partialResults: Bundle?) {
            if (!isSessionActive) return
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                val text = matches[0].trim()
                if (isValidSpeechText(text)) {
                    latestPartialText = text
                    lastSilenceStartTime = System.currentTimeMillis()
                    val totalTimeoutSec = (timeoutMs / 1000L).toInt()
                    lastEmittedSec = totalTimeoutSec
                    val currentText = getBestText()
                    scope.trySend(SpeechState.PartialResult(currentText, totalTimeoutSec))
                }
            }
        }

        fun handleError(error: Int) {
            if (!isSessionActive) return
            if (error == SpeechRecognizer.ERROR_AUDIO || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                if (getBestText().isEmpty()) {
                    isSessionActive = false
                    silenceJob?.cancel()
                    destroyCurrentRecognizer()
                    scope.trySend(SpeechState.Error("Lỗi ghi âm ($error)"))
                    return
                }
            }
            checkSilenceAndContinue()
        }

        private fun createListener(): RecognitionListener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) { handleError(error) }
            override fun onResults(results: Bundle?) { handleResults(results) }
            override fun onPartialResults(partialResults: Bundle?) { handlePartialResults(partialResults) }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        private fun createIntent(): Intent = SpeechIntentFactory.createRecognizeIntent(context)

        private fun appendRecognizedText(newText: String) {
            val trimmed = newText.trim()
            if (trimmed.isEmpty()) return
            if (accumulatedText.isNotEmpty() && !accumulatedText.endsWith(" ")) {
                accumulatedText.append(" ")
            }
            accumulatedText.append(trimmed)
        }
    }
}
