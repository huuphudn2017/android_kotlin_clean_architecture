package com.example.base.manager.navigation

import androidx.navigation.NavController
import javax.inject.Inject
import javax.inject.Singleton

/** Screen navigation for ViewModels, injected via Hilt. Bound to the NavHost in `AppScreen`. */
interface NavigatorManager {
    fun navigate(route: AppRoute)
    fun goBack()
}

@Singleton
class NavigatorManagerImpl @Inject constructor() : NavigatorManager {
    private var navController: NavController? = null

    fun bind(navController: NavController) {
        this.navController = navController
    }

    override fun navigate(route: AppRoute) {
        val controller = navController ?: return
        if (controller.currentDestination?.route == route.route) return
        controller.navigate(route.route) { launchSingleTop = true }
    }

    override fun goBack() {
        navController?.let { if (it.previousBackStackEntry != null) it.popBackStack() }
    }
}
