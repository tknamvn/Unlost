package com.dev.unlost.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import com.dev.unlost.domain.usecase.DeleteItemUseCase
import com.dev.unlost.domain.usecase.UpdateItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ItemRepository,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase
) : ViewModel() {

    val itemList: StateFlow<List<LostItem>> = repository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _itemToEdit = MutableStateFlow<LostItem?>(null)
    val itemToEdit: StateFlow<LostItem?> = _itemToEdit.asStateFlow()

    fun requestEditItem(item: LostItem) {
        _itemToEdit.value = item
    }

    fun dismissEditSheet() {
        _itemToEdit.value = null
    }

    fun saveEditedItem(updatedItem: LostItem) {
        viewModelScope.launch {
            updateItemUseCase(updatedItem)
            dismissEditSheet()
        }
    }

    fun deleteItem(item: LostItem) {
        viewModelScope.launch {
            deleteItemUseCase(item)
        }
    }

    fun undoDeleteItem(item: LostItem) {
        viewModelScope.launch {
            updateItemUseCase(item)
        }
    }
}
