package com.dev.unlost.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("unlost_onboarding_prefs", Context.MODE_PRIVATE)

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_IS_FIRST_LAUNCH, true)
    }

    fun completeOnboarding() {
        prefs.edit().putBoolean(KEY_IS_FIRST_LAUNCH, false).apply()
    }

    companion object {
        private const val KEY_IS_FIRST_LAUNCH = "key_is_first_launch"
    }
}
