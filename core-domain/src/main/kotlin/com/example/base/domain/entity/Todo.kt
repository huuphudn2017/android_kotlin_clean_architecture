package com.example.base.domain.entity

data class Todo(
    val id: Long = 0,
    val title: String,
    val isDone: Boolean = false,
    /** Epoch millis. */
    val createdAt: Long,
)

enum class TodoFilter { ALL, ACTIVE, DONE }
