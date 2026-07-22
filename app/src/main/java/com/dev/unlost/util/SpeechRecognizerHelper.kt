package com.dev.unlost.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

sealed class SpeechState {
    object Idle : SpeechState()
    object Listening : SpeechState()
    data class Success(val result: String) : SpeechState()
    data class Error(val message: String) : SpeechState()
}

@Singleton
class SpeechRecognizerHelper @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    fun startListening(): Flow<SpeechState> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(SpeechState.Error("Tính năng nhận diện giọng nói không hỗ trợ trên thiết bị này"))
            close()
            return@callbackFlow
        }

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        var silenceJob: Job? = null

        fun resetSilenceTimer() {
            silenceJob?.cancel()
            silenceJob = launch {
                delay(3000L) // 3 seconds silence timeout
                try {
                    speechRecognizer.stopListening()
                } catch (_: Exception) {}
            }
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 3000L)
            putExtra("android.speech.extras.SPEECH_INPUT_MINIMUM_SPEECH_LENGTH_MILLIS", 3000L)
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(SpeechState.Listening)
                resetSilenceTimer()
            }

            override fun onBeginningOfSpeech() {
                resetSilenceTimer()
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (rmsdB > 2.0f) {
                    resetSilenceTimer()
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                silenceJob?.cancel()
            }

            override fun onError(error: Int) {
                silenceJob?.cancel()
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Lỗi ghi âm"
                    SpeechRecognizer.ERROR_NO_MATCH -> "Không nghe thấy giọng nói"
                    SpeechRecognizer.ERROR_NETWORK -> "Lỗi kết nối mạng"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Đã hết thời gian chờ giọng nói"
                    else -> "Lỗi nhận diện giọng nói ($error)"
                }
                trySend(SpeechState.Error(errorMsg))
            }

            override fun onResults(results: Bundle?) {
                silenceJob?.cancel()
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    trySend(SpeechState.Success(matches[0]))
                } else {
                    trySend(SpeechState.Error("Không tìm thấy kết quả"))
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(listener)
        speechRecognizer.startListening(intent)

        awaitClose {
            silenceJob?.cancel()
            speechRecognizer.destroy()
        }
    }
}
