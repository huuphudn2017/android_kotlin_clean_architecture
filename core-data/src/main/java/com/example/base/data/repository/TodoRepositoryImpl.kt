package com.example.base.data.repository

import com.example.base.data.local.TodoDao
import com.example.base.data.local.toEntity
import com.example.base.domain.entity.Todo
import com.example.base.domain.repository.ITodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val dao: TodoDao,
) : ITodoRepository {
    override fun observeTodos(): Flow<List<Todo>> = dao.observeAll().map { list -> list.map { it.toDomain() } }
    override suspend fun add(todo: Todo): Long = dao.insert(todo.toEntity())
    override suspend fun update(todo: Todo) = dao.update(todo.toEntity())
    override suspend fun delete(id: Long) = dao.delete(id)
    override suspend fun deleteDone() = dao.deleteDone()
}
