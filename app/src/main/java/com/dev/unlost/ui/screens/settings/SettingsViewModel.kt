package com.dev.unlost.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.dev.unlost.util.SecurityPinManager
import com.dev.unlost.util.VoiceSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val pinManager: SecurityPinManager,
    private val voiceSettingsManager: VoiceSettingsManager
) : ViewModel() {

    val isAutoStopMicEnabled: StateFlow<Boolean> = voiceSettingsManager.isAutoStopMicEnabled
    val autoStopTimeoutSeconds: StateFlow<Int> = voiceSettingsManager.autoStopTimeoutSeconds

    private val _isPinLockEnabled = MutableStateFlow(pinManager.isPinLockEnabled())
    val isPinLockEnabled: StateFlow<Boolean> = _isPinLockEnabled.asStateFlow()

    fun refreshPinLockState() {
        _isPinLockEnabled.value = pinManager.isPinLockEnabled()
    }

    fun onAutoStopMicToggleChanged(enabled: Boolean) {
        voiceSettingsManager.setAutoStopMicEnabled(enabled)
    }

    fun onAutoStopTimeoutChanged(seconds: Int) {
        voiceSettingsManager.setAutoStopTimeoutSeconds(seconds)
    }

    fun onPinToggleChanged(enabled: Boolean, onNavigateToSetPin: () -> Unit) {
        if (enabled) {
            pinManager.clearPin()
            _isPinLockEnabled.value = false
            onNavigateToSetPin()
        } else {
            pinManager.clearPin()
            _isPinLockEnabled.value = false
        }
    }
}
