package com.dev.unlost.ui.screens.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinHeaderSection(
    uiState: PinUiState,
    shouldShake: Boolean,
    onShakeComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(shouldShake) {
        if (shouldShake) {
            val shakeSequence = listOf(-24f, 24f, -16f, 16f, -8f, 8f, 0f)
            for (target in shakeSequence) {
                shakeOffset.animateTo(target, animationSpec = tween(durationMillis = 40))
            }
            onShakeComplete()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Bảo mật",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(34.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (uiState.isCreateMode) "Tạo mã PIN bảo mật" else "Nhập mã PIN bảo mật",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Xác thực để truy cập dữ liệu vị trí đồ đạc cá nhân",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 36.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.offset { IntOffset(shakeOffset.value.toInt(), 0) }
        ) {
            PinDotIndicators(enteredPinLength = uiState.enteredPin.length)
        }

        Spacer(modifier = Modifier.height(10.dp))

        PinStatusMessage(uiState = uiState)
    }
}

@Composable
private fun PinDotIndicators(enteredPinLength: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..4) {
            val isFilled = i <= enteredPinLength
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .then(
                        if (isFilled) {
                            Modifier.background(MaterialTheme.colorScheme.primary)
                        } else {
                            Modifier
                                .background(Color.Transparent)
                                .border(width = 2.5.dp, color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                        }
                    )
            )
        }
    }
}

@Composable
private fun PinStatusMessage(uiState: PinUiState) {
    when {
        uiState.isPermanentlyLocked -> {
            Text(
                text = "Ứng dụng bị khóa vĩnh viễn.\nVui lòng khôi phục bằng SĐT!",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
        uiState.lockoutRemainingSeconds > 0 -> {
            val mins = uiState.lockoutRemainingSeconds / 60
            val secs = uiState.lockoutRemainingSeconds % 60
            val formattedTime = String.format("%02d:%02d", mins, secs)
            Text(
                text = "Thử sai quá số lần. Tạm khóa: $formattedTime",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        }
        uiState.errorMessage != null -> {
            Text(
                text = uiState.errorMessage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.error
            )
        }
        !uiState.isCreateMode && uiState.remainingAttempts < 4 -> {
            Text(
                text = "Còn ${uiState.remainingAttempts} lần nhập",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
