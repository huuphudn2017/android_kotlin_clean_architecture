package com.example.base.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.base.domain.entity.Todo

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    @ColumnInfo(name = "is_done") val isDone: Boolean,
    @ColumnInfo(name = "created_at") val createdAt: Long,
) {
    fun toDomain() = Todo(id = id, title = title, isDone = isDone, createdAt = createdAt)
}

fun Todo.toEntity() = TodoEntity(id = id, title = title, isDone = isDone, createdAt = createdAt)
