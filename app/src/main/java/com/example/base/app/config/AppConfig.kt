package com.example.base.app.config

import com.example.base.BuildConfig

/**
 * Single source of build-time configuration. Values come from `buildConfigField` in
 * `app/build.gradle.kts`; read them here instead of using `BuildConfig` directly elsewhere.
 */
object AppConfig {
    val apiBaseUrl: String = BuildConfig.API_BASE_URL
    val isDebug: Boolean = BuildConfig.DEBUG
    val versionName: String = BuildConfig.VERSION_NAME
}
