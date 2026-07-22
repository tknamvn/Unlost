package com.dev.unlost.domain.usecase

import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import javax.inject.Inject

class AddItemUseCase @Inject constructor(
    private val repository: ItemRepository
) {
    suspend operator fun invoke(item: LostItem): Long {
        require(item.title.isNotBlank()) { "Tên đồ vật không được để trống" }
        require(item.location.isNotBlank()) { "Vị trí không được để trống" }
        return repository.insertItem(item)
    }
}
