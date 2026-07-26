package com.dev.unlost.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("unlost_language_prefs", Context.MODE_PRIVATE)

    private val _currentLanguage = MutableStateFlow(getSelectedLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun getSelectedLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, LANG_VI) ?: LANG_VI
    }

    fun setLanguage(langCode: String) {
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply()
        _currentLanguage.value = langCode
    }

    companion object {
        const val LANG_VI = "vi"
        const val LANG_EN = "en"
        private const val KEY_LANGUAGE = "key_selected_language"
    }
}
