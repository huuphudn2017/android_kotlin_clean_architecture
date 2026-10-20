package com.example.base.presentation.todo

import com.example.base.domain.entity.Todo
import com.example.base.domain.entity.TodoFilter

data class TodoState(
    val isLoading: Boolean = true,
    val items: List<Todo> = emptyList(),
    val filter: TodoFilter = TodoFilter.ALL,
    val remainingCount: Int = 0,
    val hasDone: Boolean = false,
    /** `null` = editor closed. */
    val editor: TodoEditor? = null,
)

/** Editor for a new todo (`todo == null`) or an existing one. */
data class TodoEditor(
    val todo: Todo? = null,
    val text: String = todo?.title.orEmpty(),
)

/** One-off UI events (snackbar, etc.). Kept out of [TodoState] so they fire once. */
sealed interface TodoEvent {
    data class Deleted(val todo: Todo) : TodoEvent
}
