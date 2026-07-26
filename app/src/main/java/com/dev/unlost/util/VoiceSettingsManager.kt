package com.dev.unlost.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceSettingsManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isAutoStopMicEnabled = MutableStateFlow(getAutoStopMicEnabled())
    val isAutoStopMicEnabled: StateFlow<Boolean> = _isAutoStopMicEnabled.asStateFlow()

    private val _autoStopTimeoutSeconds = MutableStateFlow(getAutoStopTimeoutSeconds())
    val autoStopTimeoutSeconds: StateFlow<Int> = _autoStopTimeoutSeconds.asStateFlow()

    fun getAutoStopMicEnabled(): Boolean {
        return prefs.getBoolean(KEY_AUTO_STOP_MIC, true)
    }

    fun setAutoStopMicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_STOP_MIC, enabled).apply()
        _isAutoStopMicEnabled.value = enabled
    }

    fun getAutoStopTimeoutSeconds(): Int {
        return prefs.getInt(KEY_AUTO_STOP_TIMEOUT, 5)
    }

    fun setAutoStopTimeoutSeconds(seconds: Int) {
        val clamped = seconds.coerceIn(2, 10)
        prefs.edit().putInt(KEY_AUTO_STOP_TIMEOUT, clamped).apply()
        _autoStopTimeoutSeconds.value = clamped
    }

    companion object {
        private const val PREFS_NAME = "unlost_voice_settings_prefs"
        private const val KEY_AUTO_STOP_MIC = "key_auto_stop_mic"
        private const val KEY_AUTO_STOP_TIMEOUT = "key_auto_stop_timeout"
    }
}
