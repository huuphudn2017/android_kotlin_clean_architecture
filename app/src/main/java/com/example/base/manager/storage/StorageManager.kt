package com.example.base.manager.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.base.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

/**
 * The only access point to app settings (DataStore). ViewModels call it directly.
 * Per setting: a `xxxFlow` and a `suspend setXxx()`; defaults live here. Read once with `.first()`.
 * DataStore does its own I/O off the main thread.
 */
object StorageManager {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
    }

    val themeModeFlow: Flow<ThemeMode>
        get() = flow(Keys.THEME_MODE, ThemeMode.SYSTEM.name).map(ThemeMode::fromName)

    suspend fun setThemeMode(mode: ThemeMode) = set(Keys.THEME_MODE, mode.name)

    val dynamicColorFlow: Flow<Boolean>
        get() = flow(Keys.DYNAMIC_COLOR, false)

    suspend fun setDynamicColor(enabled: Boolean) = set(Keys.DYNAMIC_COLOR, enabled)

    private fun <T> flow(key: Preferences.Key<T>, default: T): Flow<T> =
        appContext.dataStore.data.map { it[key] ?: default }

    private suspend fun <T> set(key: Preferences.Key<T>, value: T) {
        appContext.dataStore.edit { it[key] = value }
    }
}
