package com.dev.unlost.ui.screens.pin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.unlost.util.SecurityPinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinUiState(
    val enteredPin: String = "",
    val isCreateMode: Boolean = false,
    val remainingAttempts: Int = 4,
    val lockoutRemainingSeconds: Long = 0L,
    val isPermanentlyLocked: Boolean = false,
    val errorMessage: String? = null,
    val showRecoverySheet: Boolean = false
)

sealed class PinUiEvent {
    object NavigateToHome : PinUiEvent()
    object TriggerShake : PinUiEvent()
}

@HiltViewModel
class PinViewModel @Inject constructor(
    private val pinManager: SecurityPinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinUiState())
    val uiState: StateFlow<PinUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PinUiEvent>()
    val uiEvent: SharedFlow<PinUiEvent> = _uiEvent.asSharedFlow()

    private var countdownJob: Job? = null

    init {
        val hasPin = pinManager.hasPinSet()
        _uiState.value = _uiState.value.copy(
            isCreateMode = !hasPin,
            remainingAttempts = 4 - pinManager.getFailedAttempts()
        )
        checkLockoutStatus()
    }

    private fun checkLockoutStatus() {
        if (pinManager.isPermanentlyLocked()) {
            _uiState.value = _uiState.value.copy(isPermanentlyLocked = true)
            return
        }
        val remainingSecs = pinManager.getRemainingLockoutSeconds()
        if (remainingSecs > 0) {
            startLockoutCountdown(remainingSecs)
        }
    }

    private fun startLockoutCountdown(seconds: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var current = seconds
            while (current > 0) {
                _uiState.value = _uiState.value.copy(lockoutRemainingSeconds = current)
                delay(1000L)
                current--
            }
            _uiState.value = _uiState.value.copy(
                lockoutRemainingSeconds = 0L,
                remainingAttempts = 4
            )
        }
    }

    fun onDigitClick(digit: String) {
        if (_uiState.value.lockoutRemainingSeconds > 0 || _uiState.value.isPermanentlyLocked) return

        val currentPin = _uiState.value.enteredPin
        if (currentPin.length < 4) {
            val newPin = currentPin + digit
            _uiState.value = _uiState.value.copy(enteredPin = newPin, errorMessage = null)
            if (newPin.length == 4) {
                processPinSubmission(newPin)
            }
        }
    }

    fun onBackspaceClick() {
        if (_uiState.value.lockoutRemainingSeconds > 0 || _uiState.value.isPermanentlyLocked) return

        val currentPin = _uiState.value.enteredPin
        if (currentPin.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(enteredPin = currentPin.dropLast(1))
        }
    }

    private fun processPinSubmission(pin: String) {
        if (_uiState.value.isCreateMode) {
            pinManager.savePin(pin)
            viewModelScope.launch { _uiEvent.emit(PinUiEvent.NavigateToHome) }
        } else {
            if (pinManager.verifyPin(pin)) {
                pinManager.resetLockoutState()
                viewModelScope.launch { _uiEvent.emit(PinUiEvent.NavigateToHome) }
            } else {
                viewModelScope.launch { _uiEvent.emit(PinUiEvent.TriggerShake) }
                handleWrongPin()
            }
        }
    }

    private fun handleWrongPin() {
        when (val status = pinManager.recordFailedAttempt()) {
            is SecurityPinManager.LockoutStatus.Active -> {
                _uiState.value = _uiState.value.copy(
                    enteredPin = "",
                    remainingAttempts = status.remainingAttempts,
                    errorMessage = "Sai mã PIN. Còn ${status.remainingAttempts} lần nhập"
                )
            }
            is SecurityPinManager.LockoutStatus.TemporarilyLocked -> {
                _uiState.value = _uiState.value.copy(enteredPin = "")
                startLockoutCountdown(status.seconds)
            }
            is SecurityPinManager.LockoutStatus.PermanentlyLocked -> {
                _uiState.value = _uiState.value.copy(
                    enteredPin = "",
                    isPermanentlyLocked = true
                )
            }
        }
    }

    fun toggleRecoverySheet(show: Boolean) {
        _uiState.value = _uiState.value.copy(showRecoverySheet = show)
    }

    fun resetPinViaPhone(newPin: String, phone: String) {
        pinManager.savePin(newPin, phone)
        pinManager.resetLockoutState()
        countdownJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isCreateMode = false,
            enteredPin = "",
            remainingAttempts = 4,
            lockoutRemainingSeconds = 0L,
            isPermanentlyLocked = false,
            showRecoverySheet = false
        )
        viewModelScope.launch { _uiEvent.emit(PinUiEvent.NavigateToHome) }
    }
}
