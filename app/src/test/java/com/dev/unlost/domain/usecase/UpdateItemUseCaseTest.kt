package com.dev.unlost.domain.usecase

import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateItemUseCaseTest {

    private lateinit var repository: ItemRepository
    private lateinit var updateItemUseCase: UpdateItemUseCase

    @Before
    fun setUp() {
        repository = mock(ItemRepository::class.java)
        updateItemUseCase = UpdateItemUseCase(repository)
    }

    @Test
    fun invoke_validItem_callsInsertAndReturnsId() {
        runBlocking {
            val item = LostItem(id = 1, title = "Thẻ ngân hàng", location = "Ví màu xanh")
            whenever(repository.insertItem(item)).thenReturn(1L)

            val id = updateItemUseCase(item)
            assertEquals(1L, id)
            verify(repository).insertItem(item)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun invoke_blankTitle_throwsException() {
        runBlocking {
            val item = LostItem(id = 1, title = "", location = "Ví màu xanh")
            updateItemUseCase(item)
        }
    }
}
