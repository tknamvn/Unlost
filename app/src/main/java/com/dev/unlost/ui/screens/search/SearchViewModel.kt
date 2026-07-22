package com.dev.unlost.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.usecase.DeleteItemUseCase
import com.dev.unlost.domain.usecase.SearchItemsUseCase
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
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tất cả")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val categories = listOf("Tất cả", "Chìa khóa", "Ví", "Giấy tờ")

    private val _itemToDelete = MutableStateFlow<LostItem?>(null)
    val itemToDelete: StateFlow<LostItem?> = _itemToDelete.asStateFlow()

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
