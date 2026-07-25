package com.dev.unlost.ui.screens.pin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.dev.unlost.ui.components.InteractiveSwipeBackContainer

@Composable
fun PinScreen(
    onNavigateToHome: (createdPin: String?) -> Unit,
    onBackClick: (() -> Unit)? = null,
    targetPageContent: (@Composable () -> Unit)? = null,
    viewModel: PinViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var shouldShake by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshState()
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PinUiEvent.NavigateToHome -> onNavigateToHome(event.createdPin)
                is PinUiEvent.TriggerShake -> shouldShake = true
            }
        }
    }

    InteractiveSwipeBackContainer(
        onBack = { onBackClick?.invoke() },
        enabled = onBackClick != null,
        targetPageContent = targetPageContent
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8FAFA)
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color(0xFF0F172A)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            // Centered Compact Unit: Header, Dots, and Keypad
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                PinHeaderSection(
                    uiState = uiState,
                    shouldShake = shouldShake,
                    onShakeComplete = { shouldShake = false }
                )

                Spacer(modifier = Modifier.height(24.dp))

                val isKeypadEnabled = uiState.lockoutRemainingSeconds == 0L && !uiState.isPermanentlyLocked
                PinKeypad(
                    onDigitClick = viewModel::onDigitClick,
                    onBackspaceClick = viewModel::onBackspaceClick,
                    enabled = isKeypadEnabled
                )
            }

            // Footer Options: Forgot PIN & Offline Security Badge
            PinFooterSection(
                onForgotPinClick = { viewModel.toggleRecoverySheet(true) }
            )
        }

        // Recovery Bottom Sheet Modal
        if (uiState.showRecoverySheet) {
            PinRecoverySheet(
                onDismiss = { viewModel.toggleRecoverySheet(false) },
                onPinResetSuccess = { newPin, phone ->
                    viewModel.resetPinViaPhone(newPin, phone)
                }
            )
        }
    }
}
}

@Composable
private fun PinFooterSection(
    onForgotPinClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Quên mã PIN?",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E5E53),
            modifier = Modifier.clickable(onClick = onForgotPinClick)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Bảo mật",
                tint = Color(0xFF64748B),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Bảo mật Offline",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
