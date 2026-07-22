package com.dev.unlost.di

import android.content.Context
import com.dev.unlost.util.SpeechRecognizerHelper
import com.dev.unlost.util.VietnameseTextParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSpeechRecognizerHelper(
        @ApplicationContext context: Context
    ): SpeechRecognizerHelper {
        return SpeechRecognizerHelper(context)
    }

    @Provides
    @Singleton
    fun provideVietnameseTextParser(): VietnameseTextParser {
        return VietnameseTextParser()
    }
}
