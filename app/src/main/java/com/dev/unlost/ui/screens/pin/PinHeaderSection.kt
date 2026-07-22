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
        Spacer(modifier = Modifier.height(16.dp))

        // Top Lock Badge
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(Color(0xFF80F5D2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Bảo mật",
                tint = Color(0xFF0E5E53),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = if (uiState.isCreateMode) "Tạo mã PIN bảo mật" else "Nhập mã PIN bảo mật",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle
        Text(
            text = "Xác thực để truy cập dữ liệu vị trí đồ đạc cá nhân",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4 Dot Indicators with Shake Animation
        Box(
            modifier = Modifier.offset { IntOffset(shakeOffset.value.toInt(), 0) }
        ) {
            PinDotIndicators(enteredPinLength = uiState.enteredPin.length)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Warning / Status Message
        PinStatusMessage(uiState = uiState)
    }
}

@Composable
private fun PinDotIndicators(enteredPinLength: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..4) {
            val isFilled = i <= enteredPinLength
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .then(
                        if (isFilled) {
                            Modifier.background(Color(0xFF0E5E53))
                        } else {
                            Modifier
                                .background(Color.Transparent)
                                .border(width = 2.5.dp, color = Color(0xFF0E5E53), shape = CircleShape)
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
                color = Color(0xFFEF4444),
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
                color = Color(0xFFEF4444)
            )
        }
        uiState.errorMessage != null -> {
            Text(
                text = uiState.errorMessage,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFEF4444)
            )
        }
        !uiState.isCreateMode && uiState.remainingAttempts < 4 -> {
            Text(
                text = "Còn ${uiState.remainingAttempts} lần nhập",
                fontSize = 13.sp,
                color = Color(0xFFEAB308),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
