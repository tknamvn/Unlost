package com.dev.unlost.util

sealed class SpeechState {
    object Idle : SpeechState()
    data class Listening(val remainingSeconds: Int? = null) : SpeechState()
    data class PartialResult(val result: String, val remainingSeconds: Int? = null) : SpeechState()
    data class Success(val result: String) : SpeechState()
    data class Error(val message: String) : SpeechState()
}
