package com.dev.unlost.ui.screens.add_item

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.unlost.ui.components.InteractiveSwipeBackContainer
import com.dev.unlost.ui.components.VoiceRecordButton
import com.dev.unlost.ui.screens.add_item.components.AutoStopCountdownBadge
import com.dev.unlost.ui.screens.add_item.components.LiveSpeechTextCard
import com.dev.unlost.ui.screens.add_item.components.VoiceTopBar

@Composable
fun AddItemScreen(
    onNavigateBack: () -> Unit,
    targetPageContent: (@Composable () -> Unit)? = null,
    viewModel: AddItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) viewModel.toggleVoiceRecording()
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        viewModel.uiEvent.collect { event ->
            if (event is AddItemUiEvent.SaveSuccess) onNavigateBack()
        }
    }

    InteractiveSwipeBackContainer(
        onBack = onNavigateBack,
        targetPageContent = targetPageContent
    ) {
        AddItemContent(
            uiState = uiState,
            onBackClick = onNavigateBack,
            onPermissionRecord = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
            onTitleChange = viewModel::onTitleChange,
            onLocationChange = viewModel::onLocationChange,
            onSaveClick = viewModel::saveItem
        )
    }
}

@Composable
private fun AddItemContent(
    uiState: AddItemUiState,
    onBackClick: () -> Unit,
    onPermissionRecord: () -> Unit,
    onTitleChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            VoiceTopBar(
                isRecording = uiState.isRecording,
                onBackClick = onBackClick
            )

            RecordingAreaSection(
                uiState = uiState,
                onPermissionRecord = onPermissionRecord
            )

            RecognizedResultSheet(
                uiState = uiState,
                onTitleChange = onTitleChange,
                onLocationChange = onLocationChange,
                onSaveClick = onSaveClick
            )
        }
    }
}

@Composable
private fun ColumnScope.RecordingAreaSection(
    uiState: AddItemUiState,
    onPermissionRecord: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            AutoStopCountdownBadge(
                isRecording = uiState.isRecording,
                remainingSeconds = uiState.remainingSeconds
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            VoiceRecordButton(
                isRecording = uiState.isRecording,
                onClick = onPermissionRecord,
                buttonSize = 110.dp
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            LiveSpeechTextCard(
                isRecording = uiState.isRecording,
                spokenText = uiState.spokenText
            )
        }
    }
}
