package com.dev.unlost.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding_screen")
    object SecurityPin : Screen("security_pin_screen")
    object Home : Screen("home_screen")
    object AddItem : Screen("add_item_screen")
    object Search : Screen("search_screen")
}
