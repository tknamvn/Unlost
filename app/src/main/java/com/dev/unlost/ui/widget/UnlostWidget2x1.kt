package com.dev.unlost.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.unit.ColorProvider
import com.dev.unlost.R
import com.dev.unlost.ui.theme.WidgetDarkBg

class UnlostWidget2x1 : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                WidgetMainLayout()
            }
        }
    }
}

@Composable
private fun WidgetMainLayout() {
    val state = currentState(WidgetStateKeys.STATE) ?: WidgetStateKeys.STATE_IDLE
    val liveText = currentState(WidgetStateKeys.LIVE_TEXT).orEmpty()
    val parsedTitle = currentState(WidgetStateKeys.PARSED_TITLE).orEmpty()
    val parsedLocation = currentState(WidgetStateKeys.PARSED_LOCATION).orEmpty()
    val errorMsg = currentState(WidgetStateKeys.ERROR_MESSAGE).orEmpty()

    val cardBgRes = when (state) {
        WidgetStateKeys.STATE_RECORDING -> R.drawable.bg_widget_recording
        WidgetStateKeys.STATE_PARSED -> R.drawable.bg_widget_parsed
        else -> R.drawable.bg_widget_idle
    }

    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ImageProvider(cardBgRes))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        WidgetLeftTile(
            modifier = GlanceModifier.width(70.dp).fillMaxHeight(),
            state = state
        )
        Spacer(modifier = GlanceModifier.width(6.dp))
        Box(
            modifier = GlanceModifier
                .width(1.dp)
                .fillMaxHeight()
                .background(ColorProvider(WidgetDarkBg))
        ) {}
        Spacer(modifier = GlanceModifier.width(6.dp))
        WidgetRightContent(
            modifier = GlanceModifier.fillMaxHeight(),
            state = state,
            liveText = liveText,
            parsedTitle = parsedTitle,
            parsedLocation = parsedLocation,
            errorMsg = errorMsg
        )
    }
}

@Composable
private fun WidgetLeftTile(modifier: GlanceModifier, state: String) {
    val tileBgRes = when (state) {
        WidgetStateKeys.STATE_RECORDING -> R.drawable.bg_widget_mic_recording
        WidgetStateKeys.STATE_PARSED -> R.drawable.bg_widget_mic_parsed
        else -> R.drawable.bg_widget_mic_idle
    }

    Box(
        modifier = modifier
            .background(ImageProvider(tileBgRes))
            .clickable(actionRunCallback<ToggleMicAction>()),
        contentAlignment = Alignment.Center
    ) {
        val iconRes = when (state) {
            WidgetStateKeys.STATE_PARSED, WidgetStateKeys.STATE_SAVED -> R.drawable.ic_widget_check_circle
            else -> R.drawable.ic_widget_mic
        }
        Image(
            provider = ImageProvider(iconRes),
            contentDescription = "Widget Icon",
            modifier = GlanceModifier.size(28.dp)
        )
    }
}

@Composable
private fun WidgetRightContent(
    modifier: GlanceModifier,
    state: String,
    liveText: String,
    parsedTitle: String,
    parsedLocation: String,
    errorMsg: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        when (state) {
            WidgetStateKeys.STATE_RECORDING -> RecordingView(liveText)
            WidgetStateKeys.STATE_PARSED -> ParsedView(parsedTitle, parsedLocation)
            WidgetStateKeys.STATE_SAVED -> SavedView()
            WidgetStateKeys.STATE_ERROR -> ErrorView(errorMsg)
            else -> IdleView()
        }
    }
}
