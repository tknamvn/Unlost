package com.dev.unlost.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dev.unlost.ui.components.BottomTab
import com.dev.unlost.ui.components.UnlostBottomNavigationBar
import com.dev.unlost.ui.screens.add_item.AddItemScreen
import com.dev.unlost.ui.screens.home.HomeScreen
import com.dev.unlost.ui.screens.onboarding.OnboardingScreen
import com.dev.unlost.ui.screens.pin.PinScreen
import com.dev.unlost.ui.screens.search.SearchScreen
import com.dev.unlost.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        Screen.Home.route -> BottomTab.HOME
        Screen.Search.route -> BottomTab.SEARCH
        Screen.Settings.route -> BottomTab.SETTINGS
        else -> null
    }

    Scaffold(
        bottomBar = {
            if (currentTab != null) {
                UnlostBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        val targetRoute = when (tab) {
                            BottomTab.HOME -> Screen.Home.route
                            BottomTab.SEARCH -> Screen.Search.route
                            BottomTab.SETTINGS -> Screen.Settings.route
                        }
                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
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
                    onNavigateToSearch = {
                        navController.navigate(Screen.Search.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) }
                )
            }
            composable(Screen.AddItem.route) {
                AddItemScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
