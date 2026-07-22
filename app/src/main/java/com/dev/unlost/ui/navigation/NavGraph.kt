package com.dev.unlost.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.unlost.ui.screens.add_item.AddItemScreen
import com.dev.unlost.ui.screens.home.HomeScreen
import com.dev.unlost.ui.screens.onboarding.OnboardingScreen
import com.dev.unlost.ui.screens.pin.PinScreen
import com.dev.unlost.ui.screens.search.SearchScreen

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
                    navController.navigate(Screen.SecurityPin.route) {
                        popUpTo(Screen.Onboarding.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.SecurityPin.route) {
            PinScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SecurityPin.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) }
            )
        }
        composable(Screen.AddItem.route) {
            AddItemScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
