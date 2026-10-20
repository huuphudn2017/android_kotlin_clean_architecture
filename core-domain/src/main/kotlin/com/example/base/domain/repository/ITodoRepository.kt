package com.example.base.domain.repository

import com.example.base.domain.entity.Todo
import kotlinx.coroutines.flow.Flow

/** Implemented in `:core-data` (`TodoRepositoryImpl`), bound in `app/di/RepositoryModule`. */
interface ITodoRepository {
    fun observeTodos(): Flow<List<Todo>>
    suspend fun add(todo: Todo): Long
    suspend fun update(todo: Todo)
    suspend fun delete(id: Long)
    suspend fun deleteDone()
}
