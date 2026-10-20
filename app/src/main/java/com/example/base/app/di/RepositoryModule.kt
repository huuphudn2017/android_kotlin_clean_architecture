package com.example.base.app.di

import com.example.base.data.repository.TodoRepositoryImpl
import com.example.base.domain.repository.ITodoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Binds `:core-domain` repository interfaces to their `:core-data` implementations. */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTodoRepository(impl: TodoRepositoryImpl): ITodoRepository
}
