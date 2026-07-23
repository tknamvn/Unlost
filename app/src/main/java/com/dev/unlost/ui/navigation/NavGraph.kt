package com.dev.unlost.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dev.unlost.ui.screens.add_item.AddItemScreen
import com.dev.unlost.ui.screens.main.MainPagerScreen
import com.dev.unlost.ui.screens.onboarding.OnboardingScreen
import com.dev.unlost.ui.screens.onboarding.SecurityChoiceScreen
import com.dev.unlost.ui.screens.onboarding.SecurityCustomScreen
import com.dev.unlost.ui.screens.pin.PinScreen
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
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
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
                    },
                    targetPageContent = {
                        OnboardingScreen(onStartClick = {})
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
                    },
                    targetPageContent = {
                        SecurityChoiceScreen(
                            onBackClick = {},
                            onSelectSetPin = {},
                            onSelectSkip = {}
                        )
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
                    } else null,
                    targetPageContent = if (fromOnboarding) {
                        { SecurityChoiceScreen(onBackClick = {}, onSelectSetPin = {}, onSelectSkip = {}) }
                    } else null
                )
            }

            composable(Screen.Home.route) {
                MainPagerScreen(
                    onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) }
                )
            }
            composable(Screen.Search.route) {
                MainPagerScreen(
                    onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) }
                )
            }
            composable(Screen.Settings.route) {
                MainPagerScreen(
                    onNavigateToAddItem = { navController.navigate(Screen.AddItem.route) }
                )
            }
            composable(Screen.AddItem.route) {
                AddItemScreen(
                    onNavigateBack = { navController.popBackStack() },
                    targetPageContent = {
                        MainPagerScreen(onNavigateToAddItem = {})
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
