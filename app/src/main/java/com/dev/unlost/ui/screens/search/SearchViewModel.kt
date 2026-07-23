package com.dev.unlost.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.usecase.DeleteItemUseCase
import com.dev.unlost.domain.usecase.SearchItemsUseCase
import com.dev.unlost.domain.usecase.UpdateItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchItemsUseCase: SearchItemsUseCase,
    private val deleteItemUseCase: DeleteItemUseCase,
    private val updateItemUseCase: UpdateItemUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tất cả")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = listOf("Tất cả", "Chìa khóa", "Ví", "Giấy tờ")

    private val _itemToEdit = MutableStateFlow<LostItem?>(null)
    val itemToEdit: StateFlow<LostItem?> = _itemToEdit.asStateFlow()

    val searchResults: StateFlow<List<LostItem>> = combine(_searchQuery, _selectedCategory) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        val effectiveQuery = if (category != "Tất cả" && query.isEmpty()) category else query
        searchItemsUseCase(effectiveQuery)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

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
