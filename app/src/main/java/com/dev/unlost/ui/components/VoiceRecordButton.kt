package com.dev.unlost.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.unlost.ui.theme.ErrorRed
import com.dev.unlost.ui.theme.ErrorRedLight
import com.dev.unlost.ui.theme.UnlostTealDark
import com.dev.unlost.ui.theme.UnlostTealLight
import com.dev.unlost.ui.theme.UnlostTealPrimary

@Composable
fun VoiceRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 100.dp
) {
    Box(
        modifier = modifier.size(buttonSize * 2.8f),
        contentAlignment = Alignment.Center
    ) {
        if (isRecording) {
            RadiatingRippleRings(maxSize = buttonSize * 2.8f)
        }

        val gradientBrush = if (isRecording) {
            Brush.verticalGradient(
                colors = listOf(
                    ErrorRedLight,
                    ErrorRed,
                    UnlostTealDark
                )
            )
        } else {
            Brush.verticalGradient(
                colors = listOf(
                    UnlostTealPrimary,
                    UnlostTealLight
                )
            )
        }

        Box(
            modifier = Modifier
                .size(buttonSize)
                .clip(CircleShape)
                .background(gradientBrush)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = if (isRecording) "Đang ghi âm" else "Ghi âm giọng nói",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(buttonSize * 0.45f)
            )
        }
    }
}

@Composable
private fun RadiatingRippleRings(maxSize: Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "RippleTransition")
    val rippleColor = MaterialTheme.colorScheme.error

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Scale1"
    )

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Alpha1"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Scale2"
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Alpha2"
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(maxSize / 2.4f)
                .scale(scale1)
                .clip(CircleShape)
                .background(rippleColor.copy(alpha = alpha1))
        )
        Box(
            modifier = Modifier
                .size(maxSize / 2.4f)
                .scale(scale2)
                .clip(CircleShape)
                .background(rippleColor.copy(alpha = alpha2))
        )
    }
}
