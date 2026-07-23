package com.dev.unlost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.dev.unlost.ui.navigation.NavGraph
import com.dev.unlost.ui.navigation.Screen
import com.dev.unlost.ui.theme.UnlostTheme
import com.dev.unlost.util.OnboardingManager
import com.dev.unlost.util.SecurityPinManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var onboardingManager: OnboardingManager

    @Inject
    lateinit var pinManager: SecurityPinManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialDestination = when {
            onboardingManager.isFirstLaunch() -> Screen.Onboarding.route
            pinManager.isPinLockEnabled() -> Screen.SecurityPin.route
            else -> Screen.Home.route
        }

        setContent {
            UnlostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(startDestination = initialDestination)
                }
            }
        }
    }
}