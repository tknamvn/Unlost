package com.dev.unlost.ui.screens.add_item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.usecase.AddItemUseCase
import com.dev.unlost.domain.usecase.ParseVoiceInputUseCase
import com.dev.unlost.util.SpeechRecognizerHelper
import com.dev.unlost.util.SpeechState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddItemUiState(
    val title: String = "",
    val location: String = "",
    val description: String = "",
    val isRecording: Boolean = false,
    val errorMessage: String? = null
)

sealed class AddItemUiEvent {
    object SaveSuccess : AddItemUiEvent()
    data class ShowToast(val message: String) : AddItemUiEvent()
}

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val addItemUseCase: AddItemUseCase,
    private val parseVoiceInputUseCase: ParseVoiceInputUseCase,
    private val speechRecognizerHelper: SpeechRecognizerHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddItemUiState())
    val uiState: StateFlow<AddItemUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddItemUiEvent>()
    val uiEvent: SharedFlow<AddItemUiEvent> = _uiEvent.asSharedFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle, errorMessage = null)
    }

    fun onLocationChange(newLocation: String) {
        _uiState.value = _uiState.value.copy(location = newLocation, errorMessage = null)
    }

    fun toggleVoiceRecording() {
        if (_uiState.value.isRecording) {
            _uiState.value = _uiState.value.copy(isRecording = false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRecording = true, errorMessage = null)
            speechRecognizerHelper.startListening().collect { state ->
                when (state) {
                    is SpeechState.Listening -> {
                        _uiState.value = _uiState.value.copy(isRecording = true, errorMessage = null)
                    }
                    is SpeechState.Success -> {
                        val parsed = parseVoiceInputUseCase(state.result)
                        _uiState.value = _uiState.value.copy(
                            title = parsed.title,
                            location = parsed.location,
                            isRecording = false
                        )
                    }
                    is SpeechState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isRecording = false,
                            errorMessage = state.message
                        )
                    }
                    is SpeechState.Idle -> {
                        _uiState.value = _uiState.value.copy(isRecording = false)
                    }
                }
            }
        }
    }

    fun saveItem() {
        val currentState = _uiState.value
        if (currentState.title.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Vui lòng nhập tên đồ vật")
            return
        }
        if (currentState.location.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Vui lòng nhập vị trí")
            return
        }

        viewModelScope.launch {
            try {
                addItemUseCase(
                    LostItem(
                        title = currentState.title.trim(),
                        location = currentState.location.trim(),
                        description = currentState.description.trim()
                    )
                )
                _uiEvent.emit(AddItemUiEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = currentState.copy(errorMessage = e.message ?: "Lỗi khi lưu đồ vật")
            }
        }
    }
}
