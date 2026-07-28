package com.dev.unlost.ui.widget

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.dev.unlost.domain.model.LostItem
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ToggleMicAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
        val currentState = prefs[WidgetStateKeys.STATE] ?: WidgetStateKeys.STATE_IDLE

        val intent = Intent(context, WidgetVoiceService::class.java).apply {
            action = if (currentState == WidgetStateKeys.STATE_RECORDING) {
                WidgetVoiceService.ACTION_STOP_RECORDING
            } else {
                WidgetVoiceService.ACTION_START_RECORDING
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}

class SaveItemAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val prefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
        val title = prefs[WidgetStateKeys.PARSED_TITLE].orEmpty()
        val location = prefs[WidgetStateKeys.PARSED_LOCATION].orEmpty()

        if (title.isNotEmpty()) {
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java
            )
            val repository = entryPoint.itemRepository()

            val item = LostItem(
                title = title,
                location = location,
                category = "General",
                timestamp = System.currentTimeMillis()
            )
            repository.insertItem(item)
            WidgetStateUpdater.updateState(context, WidgetStateKeys.STATE_SAVED)

            CoroutineScope(Dispatchers.IO).launch {
                delay(2000)
                WidgetStateUpdater.updateState(context, WidgetStateKeys.STATE_IDLE)
            }
        }
    }
}

class CancelItemAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        WidgetVoiceService.stopService(context)
        WidgetStateUpdater.updateState(context, WidgetStateKeys.STATE_IDLE)
    }
}
