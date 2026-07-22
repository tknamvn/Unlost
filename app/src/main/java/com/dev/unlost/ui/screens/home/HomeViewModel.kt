package com.dev.unlost.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import com.dev.unlost.domain.usecase.DeleteItemUseCase
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
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel() {

    val itemList: StateFlow<List<LostItem>> = repository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _itemToDelete = MutableStateFlow<LostItem?>(null)
    val itemToDelete: StateFlow<LostItem?> = _itemToDelete.asStateFlow()

    fun requestDeleteItem(item: LostItem) {
        _itemToDelete.value = item
    }

    fun dismissDeleteDialog() {
        _itemToDelete.value = null
    }

    fun confirmDeleteItem() {
        val item = _itemToDelete.value ?: return
        viewModelScope.launch {
            deleteItemUseCase(item)
            _itemToDelete.value = null
        }
    }
}
