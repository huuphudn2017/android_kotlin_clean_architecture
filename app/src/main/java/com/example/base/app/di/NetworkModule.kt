package com.example.base.app.di

import com.example.base.app.config.AppConfig
import com.example.base.manager.network.HttpClientManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Retrofit wiring. To add an API: declare `IXxxApi` in `:core-data`, then
 * `@Provides fun provideXxxApi(retrofit: Retrofit): IXxxApi = retrofit.create(IXxxApi::class.java)`.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = HttpClientManager.client.newBuilder()
        // Add auth/header interceptors here.
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(AppConfig.apiBaseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
