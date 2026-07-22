package com.dev.unlost.domain.usecase

import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import javax.inject.Inject

class DeleteItemUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(item: LostItem) {
        repository.deleteItem(item)
    }
}
