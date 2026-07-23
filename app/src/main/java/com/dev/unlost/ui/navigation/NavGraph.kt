package com.dev.unlost.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dev.unlost.ui.components.BottomTab
import com.dev.unlost.ui.components.UnlostBottomNavigationBar
import com.dev.unlost.ui.screens.add_item.AddItemScreen
import com.dev.unlost.ui.screens.home.HomeScreen
import com.dev.unlost.ui.screens.onboarding.OnboardingScreen
import com.dev.unlost.ui.screens.onboarding.SecurityChoiceScreen
import com.dev.unlost.ui.screens.onboarding.SecurityCustomScreen
import com.dev.unlost.ui.screens.pin.PinScreen
import com.dev.unlost.ui.screens.search.SearchScreen
import com.dev.unlost.ui.screens.settings.SettingsScreen
import com.dev.unlost.util.OnboardingManager
import com.dev.unlost.util.SecurityPinManager

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    val context = LocalContext.current
    val pinManager = remember { SecurityPinManager(context) }
    val onboardingManager = remember { OnboardingManager(context) }

    var draftPinEnabled by rememberSaveable { mutableStateOf(false) }
    var draftPin by rememberSaveable { mutableStateOf<String?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentTab = when (currentRoute) {
        Screen.Home.route -> BottomTab.HOME
        Screen.Search.route -> BottomTab.SEARCH
        Screen.Settings.route -> BottomTab.SETTINGS
        else -> null
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.fillMaxSize(),
            enterTransition = {
                val initialIdx = getRouteIndex(initialState.destination.route)
                val targetIdx = getRouteIndex(targetState.destination.route)
                if (targetIdx < initialIdx) {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeIn(animationSpec = tween(durationMillis = 300))
                } else {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeIn(animationSpec = tween(durationMillis = 300))
                }
            },
            exitTransition = {
                val initialIdx = getRouteIndex(initialState.destination.route)
                val targetIdx = getRouteIndex(targetState.destination.route)
                if (targetIdx < initialIdx) {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth / 3 },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(animationSpec = tween(durationMillis = 300))
                } else {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> -fullWidth / 3 },
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(animationSpec = tween(durationMillis = 300))
                }
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> -fullWidth / 3 },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onStartClick = {
                        navController.navigate(Screen.SecurityChoice.route)
                    }
                )
            }

            composable(Screen.SecurityChoice.route) {
                SecurityChoiceScreen(
                    onBackClick = { navController.popBackStack() },
                    onSelectSetPin = {
                        navController.navigate(Screen.SecurityPin.route + "?fromOnboarding=true")
                    },
                    onSelectSkip = {
                        draftPinEnabled = false
                        draftPin = null
                        navController.navigate(Screen.SecurityCustom.route)
                    }
                )
            }

            composable(Screen.SecurityCustom.route) {
                SecurityCustomScreen(
                    isPinEnabled = draftPinEnabled,
                    onBackClick = { navController.popBackStack() },
                    onRequestSetPin = {
                        navController.navigate(Screen.SecurityPin.route + "?fromOnboarding=true")
                    },
                    onDisablePin = {
                        draftPinEnabled = false
                        draftPin = null
                    },
                    onGoToHomeClick = {
                        if (draftPinEnabled && !draftPin.isNullOrEmpty()) {
                            pinManager.savePin(draftPin!!)
                            pinManager.setPinLockEnabled(true)
                        } else {
                            pinManager.clearPin()
                        }
                        onboardingManager.completeOnboarding()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(
                route = Screen.SecurityPin.route + "?fromOnboarding={fromOnboarding}",
                arguments = listOf(
                    navArgument("fromOnboarding") {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) { backStackEntry ->
                val fromOnboarding = backStackEntry.arguments?.getBoolean("fromOnboarding") ?: false
                PinScreen(
                    onNavigateToHome = { createdPin ->
                        if (fromOnboarding) {
                            if (!createdPin.isNullOrEmpty()) {
                                draftPin = createdPin
                                draftPinEnabled = true
                            }
                            navController.navigate(Screen.SecurityCustom.route) {
                                popUpTo(Screen.SecurityChoice.route) {
                                    inclusive = false
                                }
                            }
                        } else {
                            if (!createdPin.isNullOrEmpty()) {
                                pinManager.savePin(createdPin)
                                pinManager.setPinLockEnabled(true)
                            }
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.SecurityPin.route) {
                                    inclusive = true
                                }
                            }
                        }
                    },
                    onBackClick = if (fromOnboarding) {
                        { navController.popBackStack() }
                    } else null
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

        AnimatedVisibility(
            visible = currentTab != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ),
            exit = fadeOut(animationSpec = tween(200)) + slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(200)
            )
        ) {
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
    }
}

private fun getRouteIndex(route: String?): Int {
    return when {
        route == Screen.Onboarding.route -> -3
        route == Screen.SecurityChoice.route -> -2
        route == Screen.SecurityCustom.route -> -1
        route == Screen.Home.route -> 0
        route == Screen.Search.route -> 1
        route == Screen.Settings.route -> 2
        route == Screen.AddItem.route -> 3
        route?.startsWith(Screen.SecurityPin.route) == true -> -1
        else -> 0
    }
}
