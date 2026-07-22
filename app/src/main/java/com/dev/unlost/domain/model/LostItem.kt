package com.dev.unlost.domain.model

data class LostItem(
    val id: Long = 0,
    val title: String,
    val location: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = "General"
)
