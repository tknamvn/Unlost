package com.dev.unlost.ui.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition

object WidgetStateUpdater {

    suspend fun updateState(
        context: Context,
        state: String,
        liveText: String = "",
        parsedTitle: String = "",
        parsedLocation: String = "",
        errorMessage: String = ""
    ) {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(UnlostWidget2x1::class.java)

        for (glanceId in glanceIds) {
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[WidgetStateKeys.STATE] = state
                    this[WidgetStateKeys.LIVE_TEXT] = liveText
                    this[WidgetStateKeys.PARSED_TITLE] = parsedTitle
                    this[WidgetStateKeys.PARSED_LOCATION] = parsedLocation
                    this[WidgetStateKeys.ERROR_MESSAGE] = errorMessage
                }
            }
            UnlostWidget2x1().update(context, glanceId)
        }
    }
}
