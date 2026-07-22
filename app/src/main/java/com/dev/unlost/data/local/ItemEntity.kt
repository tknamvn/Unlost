package com.dev.unlost.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dev.unlost.domain.model.LostItem

@Entity(tableName = "lost_items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val location: String,
    val description: String,
    val timestamp: Long,
    val category: String
)

fun ItemEntity.toDomain(): LostItem {
    return LostItem(
        id = id,
        title = title,
        location = location,
        description = description,
        timestamp = timestamp,
        category = category
    )
}

fun LostItem.toEntity(): ItemEntity {
    return ItemEntity(
        id = id,
        title = title,
        location = location,
        description = description,
        timestamp = timestamp,
        category = category
    )
}
