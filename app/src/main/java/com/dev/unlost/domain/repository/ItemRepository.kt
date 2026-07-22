package com.dev.unlost.domain.repository

import com.dev.unlost.domain.model.LostItem
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getAllItems(): Flow<List<LostItem>>
    fun searchItems(query: String): Flow<List<LostItem>>
    suspend fun getItemById(id: Long): LostItem?
    suspend fun insertItem(item: LostItem): Long
    suspend fun deleteItem(item: LostItem)
}
