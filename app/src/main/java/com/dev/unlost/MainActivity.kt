package com.dev.unlost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
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

    private var stopTimestamp: Long = 0L
    private var isReLockRequired by mutableStateOf(false)

    private val appLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            if (pinManager.isPinLockEnabled()) {
                stopTimestamp = System.currentTimeMillis()
            }
        }

        override fun onStart(owner: LifecycleOwner) {
            if (stopTimestamp > 0L && pinManager.isPinLockEnabled()) {
                val elapsedTime = System.currentTimeMillis() - stopTimestamp
                if (elapsedTime >= 30_000L) { // 30 seconds background timeout
                    isReLockRequired = true
                }
            }
            stopTimestamp = 0L
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

        setContent {
            UnlostTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val initialDestination = when {
                        isReLockRequired -> Screen.SecurityPin.route
                        onboardingManager.isFirstLaunch() -> Screen.Onboarding.route
                        pinManager.isPinLockEnabled() -> Screen.SecurityPin.route
                        else -> Screen.Home.route
                    }

                    key(isReLockRequired) {
                        NavGraph(startDestination = initialDestination)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(appLifecycleObserver)
    }
}