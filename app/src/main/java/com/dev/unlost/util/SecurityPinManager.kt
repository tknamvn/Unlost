package com.dev.unlost.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityPinManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = createEncryptedPrefs(context)

    private fun createEncryptedPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "unlost_security_encrypted_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Throwable) {
            context.getSharedPreferences("unlost_security_prefs_fallback", Context.MODE_PRIVATE)
        }
    }

    fun isPinLockEnabled(): Boolean = prefs.getBoolean(KEY_PIN_ENABLED, false)

    fun setPinLockEnabled(enabled: Boolean) {
        if (!enabled) clearPin() else prefs.edit().putBoolean(KEY_PIN_ENABLED, true).apply()
    }

    fun clearPin() {
        prefs.edit()
            .putBoolean(KEY_PIN_ENABLED, false)
            .remove(KEY_HASHED_PIN)
            .remove(KEY_PIN_SALT)
            .remove(KEY_PHONE_NUMBER)
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putInt(KEY_LOCKOUT_TIER, 0)
            .putLong(KEY_LOCKOUT_END_TIME, 0L)
            .putBoolean(KEY_PERM_LOCKED, false)
            .apply()
    }

    fun hasPinSet(): Boolean = prefs.getString(KEY_HASHED_PIN, null) != null

    fun savePin(pin: String, phoneNumber: String = "") {
        val salt = generateSalt()
        val hashedPin = hashPin(pin, salt)
        prefs.edit()
            .putString(KEY_PIN_SALT, salt)
            .putString(KEY_HASHED_PIN, hashedPin)
            .putString(KEY_PHONE_NUMBER, phoneNumber)
            .putBoolean(KEY_PIN_ENABLED, true)
            .putInt(KEY_FAILED_ATTEMPTS, 0)
            .putInt(KEY_LOCKOUT_TIER, 0)
            .putLong(KEY_LOCKOUT_END_TIME, 0L)
            .putBoolean(KEY_PERM_LOCKED, false)
            .apply()
    }

    fun verifyPin(enteredPin: String): Boolean {
        val savedHash = prefs.getString(KEY_HASHED_PIN, null) ?: return false
        val salt = prefs.getString(KEY_PIN_SALT, null) ?: return false
        return hashPin(enteredPin, salt) == savedHash
    }

    fun getFailedAttempts(): Int = prefs.getInt(KEY_FAILED_ATTEMPTS, 0)

    fun recordFailedAttempt(): LockoutStatus {
        if (isPermanentlyLocked()) return LockoutStatus.PermanentlyLocked

        val currentFailed = getFailedAttempts() + 1
        prefs.edit().putInt(KEY_FAILED_ATTEMPTS, currentFailed).apply()

        if (currentFailed >= 4) {
            val tier = prefs.getInt(KEY_LOCKOUT_TIER, 0) + 1
            prefs.edit().putInt(KEY_LOCKOUT_TIER, tier).putInt(KEY_FAILED_ATTEMPTS, 0).apply()

            val durationSeconds = when (tier) {
                1 -> 60L
                2 -> 300L
                3 -> 900L
                4 -> 1800L
                else -> -1L
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

    private fun generateSalt(): String {
        val randomBytes = ByteArray(16)
        SecureRandom().nextBytes(randomBytes)
        return Base64.getEncoder().encodeToString(randomBytes)
    }

    private fun hashPin(pin: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest((pin + salt).toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    sealed class LockoutStatus {
        data class Active(val remainingAttempts: Int) : LockoutStatus()
        data class TemporarilyLocked(val seconds: Long) : LockoutStatus()
        object PermanentlyLocked : LockoutStatus()
    }

    companion object {
        private const val KEY_PIN_ENABLED = "key_pin_enabled"
        private const val KEY_HASHED_PIN = "key_hashed_pin"
        private const val KEY_PIN_SALT = "key_pin_salt"
        private const val KEY_PHONE_NUMBER = "key_phone_number"
        private const val KEY_FAILED_ATTEMPTS = "key_failed_attempts"
        private const val KEY_LOCKOUT_TIER = "key_lockout_tier"
        private const val KEY_LOCKOUT_END_TIME = "key_lockout_end_time"
        private const val KEY_PERM_LOCKED = "key_perm_locked"
    }
}
