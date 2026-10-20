package com.example.base.app.di

import com.example.base.manager.navigation.NavigatorManager
import com.example.base.manager.navigation.NavigatorManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigatorModule {

    @Binds
    abstract fun bindNavigator(impl: NavigatorManagerImpl): NavigatorManager
}
