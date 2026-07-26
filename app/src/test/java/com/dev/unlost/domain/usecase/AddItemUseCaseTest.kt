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

class AddItemUseCaseTest {

    private lateinit var repository: ItemRepository
    private lateinit var addItemUseCase: AddItemUseCase

    @Before
    fun setUp() {
        repository = mock(ItemRepository::class.java)
        addItemUseCase = AddItemUseCase(repository)
    }

    @Test
    fun invoke_validItem_callsInsertAndReturnsId() {
        runBlocking {
            val item = LostItem(title = "Chìa khóa", location = "Bàn làm việc")
            whenever(repository.insertItem(item)).thenReturn(1L)

            val id = addItemUseCase(item)
            assertEquals(1L, id)
            verify(repository).insertItem(item)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun invoke_blankTitle_throwsException() {
        runBlocking {
            val item = LostItem(title = "   ", location = "Bàn làm việc")
            addItemUseCase(item)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun invoke_blankLocation_throwsException() {
        runBlocking {
            val item = LostItem(title = "Chìa khóa", location = "  ")
            addItemUseCase(item)
        }
    }
}
