package com.dev.unlost.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityPinManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("unlost_security_prefs", Context.MODE_PRIVATE)

    fun isPinLockEnabled(): Boolean {
        return prefs.getBoolean(KEY_PIN_ENABLED, false)
    }

    fun setPinLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_PIN_ENABLED, enabled).apply()
    }

    fun hasPinSet(): Boolean {
        return prefs.getString(KEY_USER_PIN, null) != null
    }

    fun getSavedPin(): String? {
        return prefs.getString(KEY_USER_PIN, null)
    }

    fun savePin(pin: String, phoneNumber: String = "") {
        prefs.edit()
            .putString(KEY_USER_PIN, pin)
            .putString(KEY_PHONE_NUMBER, phoneNumber)
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putInt(KEY_LOCKOUT_TIER, 0)
            .putLong(KEY_LOCKOUT_END_TIME, 0L)
            .putBoolean(KEY_PERM_LOCKED, false)
            .apply()
    }

    fun verifyPin(enteredPin: String): Boolean {
        val savedPin = prefs.getString(KEY_USER_PIN, null)
        return savedPin != null && savedPin == enteredPin
    }

    fun getFailedAttempts(): Int = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)

    fun recordFailedAttempt(): LockoutStatus {
        if (isPermanentlyLocked()) {
            return LockoutStatus.PermanentlyLocked
        }

        val currentFailed = getFailedAttempts() + 1
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, currentFailed).apply()

        if (currentFailed >= 4) {
            val tier = prefs.getInt(KEY_LOCKOUT_TIER, 0) + 1
            prefs.edit().putInt(KEY_LOCKOUT_TIER, tier).putInt(KEY_FAILED_ATTEMPTS, 0).apply()

            val durationSeconds = when (tier) {
                1 -> 60L        // 1 min
                2 -> 300L       // 5 mins
                3 -> 900L       // 15 mins
                4 -> 1800L      // 30 mins
                else -> -1L     // Permanent
            }

            if (durationSeconds == -1L) {
                prefs.edit().putBoolean(KEY_PERM_LOCKED, true).apply()
                return LockoutStatus.PermanentlyLocked
            }

            val endTime = System.currentTimeMillis() + (durationSeconds * 1000L)
            prefs.edit().putLong(KEY_LOCKOUT_END_TIME, endTime).apply()
            return LockoutStatus.TemporarilyLocked(durationSeconds)
        }

        return LockoutStatus.Active(remainingAttempts = 4 - currentFailed)
    }

    fun getRemainingLockoutSeconds(): Long {
        if (isPermanentlyLocked()) return -1L
        val endTime = prefs.getLong(KEY_LOCKOUT_END_TIME, 0L)
        val diff = endTime - System.currentTimeMillis()
        return if (diff > 0) diff / 1000L else 0L
    }

    fun isPermanentlyLocked(): Boolean = prefs.getBoolean(KEY_PERM_LOCKED, false)

    fun resetLockoutState() {
        prefs.edit()
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putInt(KEY_LOCKOUT_TIER, 0)
            .putLong(KEY_LOCKOUT_END_TIME, 0L)
            .putBoolean(KEY_PERM_LOCKED, false)
            .apply()
    }

    sealed class LockoutStatus {
        data class Active(val remainingAttempts: Int) : LockoutStatus()
        data class TemporarilyLocked(val seconds: Long) : LockoutStatus()
        object PermanentlyLocked : LockoutStatus()
    }

    companion object {
        private const val KEY_PIN_ENABLED = "key_pin_enabled"
        private const val KEY_USER_PIN = "key_user_pin"
        private const val KEY_PHONE_NUMBER = "key_phone_number"
        private const val KEY_FAILED_ATTEMPTS = "key_failed_attempts"
        private const val KEY_LOCKOUT_TIER = "key_lockout_tier"
        private const val KEY_LOCKOUT_END_TIME = "key_lockout_end_time"
        private const val KEY_PERM_LOCKED = "key_perm_locked"
    }
}
