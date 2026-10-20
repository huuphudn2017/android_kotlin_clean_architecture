package com.example.base.presentation.setting

import com.example.base.ui.theme.ThemeMode

data class SettingState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = false,
    val versionName: String = "",
)
