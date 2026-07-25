package com.dev.unlost.di

import android.content.Context
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.dev.unlost.data.local.AppDatabase
import com.dev.unlost.data.local.ItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        val passphraseBytes = getOrCreateDbPassphrase(context)
        val factory = SupportFactory(passphraseBytes)

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unlost_database.db"
        )
            .openHelperFactory(factory)
            .build()
    }

    @Provides
    @Singleton
    fun provideItemDao(database: AppDatabase): ItemDao {
        return database.itemDao()
    }

    private fun getOrCreateDbPassphrase(context: Context): ByteArray {
        val prefs = try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                "unlost_db_security_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Throwable) {
            context.getSharedPreferences("unlost_db_security_fallback", Context.MODE_PRIVATE)
        }

        var passphraseBase64 = prefs.getString("key_db_passphrase", null)
        if (passphraseBase64 == null) {
            val randomBytes = ByteArray(32)
            SecureRandom().nextBytes(randomBytes)
            passphraseBase64 = Base64.getEncoder().encodeToString(randomBytes)
            prefs.edit().putString("key_db_passphrase", passphraseBase64).apply()
        }

        return Base64.getDecoder().decode(passphraseBase64)
    }
}
