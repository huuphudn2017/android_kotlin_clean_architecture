package com.example.base.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.base.app.config.AppConfig
import com.example.base.manager.navigation.NavigatorManager
import com.example.base.manager.storage.StorageManager
import com.example.base.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Settings go straight `ViewModel → StorageManager`; no repository layer. */
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val navigator: NavigatorManager,
) : ViewModel() {

    val state: StateFlow<SettingState> = combine(
        StorageManager.themeModeFlow,
        StorageManager.dynamicColorFlow,
    ) { themeMode, dynamicColor ->
        SettingState(themeMode = themeMode, dynamicColor = dynamicColor, versionName = AppConfig.versionName)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingState(versionName = AppConfig.versionName))

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { StorageManager.setThemeMode(mode) }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { StorageManager.setDynamicColor(enabled) }
    }

    fun goBack() = navigator.goBack()
}
