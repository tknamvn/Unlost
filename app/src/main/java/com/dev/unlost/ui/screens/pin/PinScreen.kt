package com.dev.unlost.ui.screens.pin

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val uiState by viewModel.uiState.collectAsState()
    var shouldShake by remember { mutableStateOf(false) }

    BackHandler(enabled = onBackClick == null) {
        activity?.moveTaskToBack(true)
    }

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
            color = MaterialTheme.colorScheme.background
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
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }

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

                PinFooterSection(
                    onForgotPinClick = { viewModel.toggleRecoverySheet(true) }
                )
            }

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
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onForgotPinClick)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Bảo mật",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Bảo mật Offline",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
