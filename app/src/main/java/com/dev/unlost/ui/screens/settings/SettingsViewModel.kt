package com.dev.unlost.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.dev.unlost.util.SecurityPinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val pinManager: SecurityPinManager
) : ViewModel() {

    private val _isPinLockEnabled = MutableStateFlow(pinManager.isPinLockEnabled())
    val isPinLockEnabled: StateFlow<Boolean> = _isPinLockEnabled.asStateFlow()

    fun refreshPinLockState() {
        _isPinLockEnabled.value = pinManager.isPinLockEnabled()
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
