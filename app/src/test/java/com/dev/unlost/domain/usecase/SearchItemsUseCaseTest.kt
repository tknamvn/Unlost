package com.dev.unlost.domain.usecase

import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SearchItemsUseCaseTest {

    private lateinit var repository: ItemRepository
    private lateinit var searchItemsUseCase: SearchItemsUseCase

    @Before
    fun setUp() {
        repository = mock(ItemRepository::class.java)
        searchItemsUseCase = SearchItemsUseCase(repository)
    }

    @Test
    fun invoke_blankQuery_callsGetAllItems() {
        runBlocking {
            val sampleItems = listOf(
                LostItem(id = 1, title = "Ví tiền", location = "Bàn ăn"),
                LostItem(id = 2, title = "Chìa khóa", location = "Tủ giầy")
            )
            whenever(repository.getAllItems()).thenReturn(flowOf(sampleItems))

            val result = searchItemsUseCase("   ").first()
            assertEquals(2, result.size)
            verify(repository).getAllItems()
        }
    }

    @Test
    fun invoke_nonEmptyQuery_callsSearchItems() {
        runBlocking {
            val sampleItems = listOf(
                LostItem(id = 1, title = "Ví tiền", location = "Bàn ăn")
            )
            whenever(repository.searchItems("Ví")).thenReturn(flowOf(sampleItems))

            val result = searchItemsUseCase("Ví").first()
            assertEquals(1, result.size)
            assertEquals("Ví tiền", result[0].title)
            verify(repository).searchItems("Ví")
        }
    }
}
