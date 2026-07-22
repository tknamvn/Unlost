package com.dev.unlost.domain.usecase

import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchItemsUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    operator fun invoke(query: String): Flow<List<LostItem>> {
        return if (query.isBlank()) {
            repository.getAllItems()
        } else {
            repository.searchItems(query)
        }
    }
}
