package com.dev.unlost.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.unlost.ui.screens.add_item.AddItemScreen
import com.dev.unlost.ui.screens.home.HomeScreen
import com.dev.unlost.ui.screens.onboarding.OnboardingScreen
import com.dev.unlost.ui.screens.search.SearchScreen
import com.dev.unlost.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Onboarding.route
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onStartClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) { launchSingleTop = true }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                }
            )
        }
        composable(Screen.AddItem.route) {
            AddItemScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { launchSingleTop = true }
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route) { launchSingleTop = true }
                },
                onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) { launchSingleTop = true }
                },
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) { launchSingleTop = true }
                }
            )
        }
    }
}
