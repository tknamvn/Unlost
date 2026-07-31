package com.dev.unlost.ui.widget

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.app.NotificationCompat
import com.dev.unlost.R
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WidgetVoiceService : Service() {

    private var speechRecognizer: SpeechRecognizer? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val accumulatedText = StringBuilder()
    private var latestPartialText: String = ""
    private var lastSpeechTime = System.currentTimeMillis()
    private var silenceJob: Job? = null
    private var isRecordingFinished = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundServiceNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_RECORDING) {
            finalizeAndStop()
            return START_NOT_STICKY
        }
        accumulatedText.clear()
        latestPartialText = ""
        isRecordingFinished = false
        startListeningSession()
        startSilenceChecker()
        return START_STICKY
    }

    private fun getVoiceSettings() = EntryPointAccessors.fromApplication(
        applicationContext,
        WidgetEntryPoint::class.java
    ).voiceSettingsManager()

    private fun startSilenceChecker() {
        silenceJob?.cancel()
        val voiceSettings = getVoiceSettings()
        if (!voiceSettings.getAutoStopMicEnabled()) return

        val timeoutMs = voiceSettings.getAutoStopTimeoutSeconds().coerceIn(2, 10) * 1000L
        lastSpeechTime = System.currentTimeMillis()
        silenceJob = serviceScope.launch {
            while (!isRecordingFinished) {
                val elapsed = System.currentTimeMillis() - lastSpeechTime
                if (elapsed >= timeoutMs) {
                    finalizeAndStop()
                    break
                }
                delay(100L)
            }
        }
    }

    private fun startListeningSession() {
        if (isRecordingFinished) return
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            serviceScope.launch {
                WidgetStateUpdater.updateState(applicationContext, WidgetStateKeys.STATE_ERROR, errorMessage = "Không hỗ trợ giọng nói")
                cleanUpAndStop()
            }
            return
        }

        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
            setRecognitionListener(createListener())
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer?.startListening(intent)
        serviceScope.launch {
            val currentText = getFullSpokenText().ifEmpty { "Đang lắng nghe..." }
            WidgetStateUpdater.updateState(applicationContext, WidgetStateKeys.STATE_RECORDING, liveText = currentText)
        }
    }

    private fun createListener() = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}

        override fun onError(error: Int) {
            if (isRecordingFinished) return
            handleSessionEnded()
        }

        override fun onResults(results: Bundle?) {
            if (isRecordingFinished) return
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val spokenText = matches?.firstOrNull()?.trim().orEmpty()
            if (spokenText.isNotEmpty()) {
                appendText(spokenText)
                latestPartialText = ""
                lastSpeechTime = System.currentTimeMillis()
            }
            handleSessionEnded()
        }

        override fun onPartialResults(partialResults: Bundle?) {
            if (isRecordingFinished) return
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val spokenText = matches?.firstOrNull()?.trim().orEmpty()
            if (spokenText.isNotEmpty()) {
                latestPartialText = spokenText
                lastSpeechTime = System.currentTimeMillis()
                val fullText = getFullSpokenText()
                serviceScope.launch {
                    WidgetStateUpdater.updateState(applicationContext, WidgetStateKeys.STATE_RECORDING, liveText = fullText)
                }
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun handleSessionEnded() {
        if (isRecordingFinished) return
        val voiceSettings = getVoiceSettings()
        val elapsed = System.currentTimeMillis() - lastSpeechTime
        val timeoutMs = voiceSettings.getAutoStopTimeoutSeconds().coerceIn(2, 10) * 1000L

        if (voiceSettings.getAutoStopMicEnabled() && elapsed >= timeoutMs) {
            finalizeAndStop()
        } else {
            serviceScope.launch {
                delay(50L)
                startListeningSession()
            }
        }
    }

    private fun appendText(newText: String) {
        if (accumulatedText.isNotEmpty() && !accumulatedText.endsWith(" ")) {
            accumulatedText.append(" ")
        }
        accumulatedText.append(newText)
    }

    private fun getFullSpokenText(): String {
        val builder = StringBuilder(accumulatedText)
        if (latestPartialText.isNotBlank()) {
            if (builder.isNotEmpty() && !builder.endsWith(" ")) builder.append(" ")
            builder.append(latestPartialText.trim())
        }
        return builder.toString().trim()
    }

    private fun finalizeAndStop() {
        if (isRecordingFinished) return
        isRecordingFinished = true
        silenceJob?.cancel()

        serviceScope.launch {
            val textToParse = getFullSpokenText()
            if (textToParse.isNotEmpty()) {
                val entryPoint = EntryPointAccessors.fromApplication(
                    applicationContext,
                    WidgetEntryPoint::class.java
                )
                val parsedResult = entryPoint.vietnameseTextParser().parse(textToParse)
                WidgetStateUpdater.updateState(
                    applicationContext,
                    WidgetStateKeys.STATE_PARSED,
                    textToParse,
                    parsedResult.title,
                    parsedResult.location
                )
            } else {
                WidgetStateUpdater.updateState(applicationContext, WidgetStateKeys.STATE_IDLE)
            }
            cleanUpAndStop()
        }
    }

    private fun cleanUpAndStop() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundServiceNotification() {
        val channelId = "unlost_widget_voice_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Widget Voice Recording", NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Unlost Widget Voice")
            .setContentText("Đang thu âm vị trí đồ đạc...")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        silenceJob?.cancel()
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
    }

    companion object {
        const val ACTION_START_RECORDING = "com.dev.unlost.action.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.dev.unlost.action.STOP_RECORDING"
        private const val NOTIFICATION_ID = 1002

        fun stopService(context: Context) {
            val intent = Intent(context, WidgetVoiceService::class.java).apply {
                action = ACTION_STOP_RECORDING
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }
}
