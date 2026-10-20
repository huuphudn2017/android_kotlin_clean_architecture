package com.example.base.presentation.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.base.manager.dialog.DialogHost
import com.example.base.manager.navigation.AppRoute
import com.example.base.manager.navigation.NavigatorManagerImpl
import com.example.base.presentation.setting.SettingScreen
import com.example.base.presentation.todo.TodoScreen

/** Root of the UI: the NavHost plus app-wide overlays such as [DialogHost]. */
@Composable
fun AppScreen(navigator: NavigatorManagerImpl) {
    val navController = rememberNavController()
    LaunchedEffect(navController) { navigator.bind(navController) }

    NavHost(navController = navController, startDestination = AppRoute.start.route) {
        composable(AppRoute.Todo.route) { TodoScreen() }
        composable(AppRoute.Setting.route) { SettingScreen() }
    }
    DialogHost()
}
