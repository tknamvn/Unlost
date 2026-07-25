# ProGuard Rules for Unlost App

# SQLCipher Keep Rules
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-keep class net.zetetic.database.** { *; }

# Room CSDL Keep Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt & Dependency Injection
-keep class * implements dagger.hilt.internal.GeneratedComponent
-keep class * implements dagger.hilt.internal.GeneratedComponentManager
-keep class * implements dagger.hilt.internal.TestSingletonComponent
-keep class dagger.hilt.android.internal.lifecycle.HiltViewModelFactory

# Keep Domain Data Models for Reflection/Serialization
-keep class com.dev.unlost.domain.model.** { *; }
-keep class com.dev.unlost.data.local.** { *; }
