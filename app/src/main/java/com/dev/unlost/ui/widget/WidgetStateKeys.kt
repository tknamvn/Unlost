package com.dev.unlost.ui.widget

import androidx.datastore.preferences.core.stringPreferencesKey

object WidgetStateKeys {
    val STATE = stringPreferencesKey("widget_state") // IDLE, RECORDING, PARSED, SAVED, ERROR
    val LIVE_TEXT = stringPreferencesKey("live_text")
    val PARSED_TITLE = stringPreferencesKey("parsed_title")
    val PARSED_LOCATION = stringPreferencesKey("parsed_location")
    val ERROR_MESSAGE = stringPreferencesKey("error_message")

    const val STATE_IDLE = "IDLE"
    const val STATE_RECORDING = "RECORDING"
    const val STATE_PARSED = "PARSED"
    const val STATE_SAVED = "SAVED"
    const val STATE_ERROR = "ERROR"
}
