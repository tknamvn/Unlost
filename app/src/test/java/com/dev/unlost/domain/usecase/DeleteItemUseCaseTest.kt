package com.dev.unlost.domain.usecase

import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.domain.repository.ItemRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

class DeleteItemUseCaseTest {

    private lateinit var repository: ItemRepository
    private lateinit var deleteItemUseCase: DeleteItemUseCase

    @Before
    fun setUp() {
        repository = mock(ItemRepository::class.java)
        deleteItemUseCase = DeleteItemUseCase(repository)
    }

    @Test
    fun invoke_callsRepositoryDeleteItem() {
        runBlocking {
            val item = LostItem(id = 1, title = "Ví tiền", location = "Tủ sách")
            deleteItemUseCase(item)
            verify(repository).deleteItem(item)
        }
    }
}
