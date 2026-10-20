package com.example.base.domain.usecase

import com.example.base.domain.entity.Todo
import com.example.base.domain.entity.TodoFilter
import com.example.base.domain.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoUseCaseTest {

    private class FakeRepository : ITodoRepository {
        val items = MutableStateFlow<List<Todo>>(emptyList())
        private var nextId = 1L
        override fun observeTodos(): Flow<List<Todo>> = items
        override suspend fun add(todo: Todo): Long {
            val id = nextId++
            items.update { it + todo.copy(id = id) }
            return id
        }
        override suspend fun update(todo: Todo) = items.update { list -> list.map { if (it.id == todo.id) todo else it } }
        override suspend fun delete(id: Long) = items.update { list -> list.filterNot { it.id == id } }
        override suspend fun deleteDone() = items.update { list -> list.filterNot { it.isDone } }
    }

    private val repo = FakeRepository()
    private val useCase = TodoUseCase(repo)

    @Test
    fun `blank titles are rejected and titles are trimmed`() = runTest {
        assertFalse(useCase.add("   "))
        assertTrue(useCase.add("  Buy milk  "))
        assertEquals("Buy milk", repo.items.value.single().title)
    }

    @Test
    fun `active items come first, newest first`() = runTest {
        useCase.add("old", now = 1)
        useCase.add("new", now = 2)
        useCase.toggle(repo.items.value.first { it.title == "new" })
        useCase.add("newest", now = 3)

        val titles = useCase.observe(TodoFilter.ALL).first().map { it.title }
        assertEquals(listOf("newest", "old", "new"), titles)
    }

    @Test
    fun `filters and clearDone`() = runTest {
        useCase.add("a", now = 1)
        useCase.add("b", now = 2)
        useCase.toggle(repo.items.value.first { it.title == "a" })

        assertEquals(listOf("b"), useCase.observe(TodoFilter.ACTIVE).first().map { it.title })
        assertEquals(listOf("a"), useCase.observe(TodoFilter.DONE).first().map { it.title })

        useCase.clearDone()
        assertEquals(listOf("b"), repo.items.value.map { it.title })
    }
}
