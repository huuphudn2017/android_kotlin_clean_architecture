package com.example.base.manager.navigation

/**
 * All screen routes. Routes with arguments declare a placeholder and a builder (`create`);
 * never concatenate route strings at call sites.
 */
sealed class AppRoute(val route: String) {
    data object Todo : AppRoute("todo")
    data object Setting : AppRoute("setting")

    companion object {
        val start: AppRoute = Todo
    }
}
