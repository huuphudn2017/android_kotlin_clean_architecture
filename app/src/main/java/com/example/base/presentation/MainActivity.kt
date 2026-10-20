package com.example.base.presentation

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.base.manager.navigation.NavigatorManagerImpl
import com.example.base.manager.storage.StorageManager
import com.example.base.presentation.app.AppScreen
import com.example.base.ui.theme.AppTheme
import com.example.base.ui.theme.ThemeMode
import com.example.base.ui.theme.isDark
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: NavigatorManagerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by StorageManager.themeModeFlow.collectAsStateWithLifecycle(ThemeMode.SYSTEM)
            val dynamicColor by StorageManager.dynamicColorFlow.collectAsStateWithLifecycle(false)
            val dark = themeMode.isDark()

            // Status/navigation bar icons follow the app theme, not the system one.
            DisposableEffect(dark) {
                val style = if (dark) SystemBarStyle.dark(Color.TRANSPARENT)
                else SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose {}
            }

            AppTheme(themeMode = themeMode, dynamicColor = dynamicColor) {
                AppScreen(navigator)
            }
        }
    }
}
