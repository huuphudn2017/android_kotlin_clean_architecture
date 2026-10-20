package com.example.base.domain.usecase

import com.example.base.domain.entity.Todo
import com.example.base.domain.entity.TodoFilter
import com.example.base.domain.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/** Business rules for todos: validation, filtering and ordering live here, not in the ViewModel. */
class TodoUseCase @Inject constructor(
    private val repository: ITodoRepository,
) {
    /** Active items first, newest first within each group. */
    fun observe(filter: TodoFilter): Flow<List<Todo>> =
        repository.observeTodos().map { todos -> todos.filterBy(filter).sortedForDisplay() }

    /** Returns `false` when the title is blank. */
    suspend fun add(title: String, now: Long = System.currentTimeMillis()): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return false
        repository.add(Todo(title = trimmed.take(MAX_TITLE_LENGTH), createdAt = now))
        return true
    }

    suspend fun toggle(todo: Todo) = repository.update(todo.copy(isDone = !todo.isDone))

    suspend fun rename(todo: Todo, title: String): Boolean {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return false
        repository.update(todo.copy(title = trimmed.take(MAX_TITLE_LENGTH)))
        return true
    }

    suspend fun delete(todo: Todo) = repository.delete(todo.id)

    /** Puts a deleted todo back with its original id (undo). */
    suspend fun restore(todo: Todo) {
        repository.add(todo)
    }

    suspend fun clearDone() = repository.deleteDone()

    companion object {
        const val MAX_TITLE_LENGTH = 200
    }
}

internal fun List<Todo>.filterBy(filter: TodoFilter): List<Todo> = when (filter) {
    TodoFilter.ALL -> this
    TodoFilter.ACTIVE -> filterNot { it.isDone }
    TodoFilter.DONE -> filter { it.isDone }
}

internal fun List<Todo>.sortedForDisplay(): List<Todo> =
    sortedWith(compareBy<Todo> { it.isDone }.thenByDescending { it.createdAt })
