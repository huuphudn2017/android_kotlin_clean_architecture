package com.example.base.manager.network

import com.example.base.app.config.AppConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * The one shared [OkHttpClient]. Anything that talks HTTP (Retrofit, image loading, downloads,
 * WebSocket) should derive from [client] with `newBuilder()`, which shares the connection pool
 * and dispatcher. Never build a fresh client per call.
 */
object HttpClientManager {

    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (AppConfig.isDebug) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                }
            )
            .build()
    }
}
