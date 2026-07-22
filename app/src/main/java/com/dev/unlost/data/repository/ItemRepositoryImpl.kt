package com.dev.unlost.data.repository

import com.dev.unlost.data.local.ItemDao
import com.dev.unlost.data.local.toDomain
import com.dev.unlost.data.local.toEntity
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao
) : ItemRepository {

    override fun getAllItems(): Flow<List<LostItem>> {
        return itemDao.getAllItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchItems(query: String): Flow<List<LostItem>> {
        return itemDao.searchItems(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getItemById(id: Long): LostItem? {
        return itemDao.getItemById(id)?.toDomain()
    }

    override suspend fun insertItem(item: LostItem): Long {
        return itemDao.insertItem(item.toEntity())
    }

    override suspend fun deleteItem(item: LostItem) {
        itemDao.deleteItem(item.toEntity())
    }
}
