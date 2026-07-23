package com.dev.unlost.ui.screens.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeVoiceBanner(onBannerClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    animProgress.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 100, easing = FastOutSlowInEasing)
                    )
                }
                is PressInteraction.Release, is PressInteraction.Cancel -> {
                    animProgress.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }
        }
    }

    val progress = animProgress.value
    val scale = 1.0f - (0.05f * progress)
    val micScale = 1.0f - (0.08f * progress)
    val elevation = (6.dp * (1f - progress)) + 1.dp

    val textColor = lerp(Color(0xFF0E5E53), Color.White, progress)
    val subTextColor = lerp(Color(0xFF0E5E53), Color(0xFFD1FAE5), progress)
    val micBgColor = lerp(Color(0xFF0E5E53), Color.White, progress)
    val micIconColor = lerp(Color.White, Color(0xFF0E5E53), progress)

    val bannerShape = RoundedCornerShape(32.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(bannerShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onBannerClick
            ),
        shape = bannerShape,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val centerColor = lerp(Color(0xFF76F7D4), Color(0xFF065F46), progress)
                    val edgeColor = lerp(Color(0xFF76F7D4), Color(0xFF047857), progress)
                    val radius = size.maxDimension * (0.6f + 0.4f * progress)
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(centerColor, edgeColor),
                            center = center,
                            radius = radius
                        )
                    )
                }
                .padding(vertical = 30.dp, horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bạn đang để đồ ở đâu?",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .graphicsLayer {
                            scaleX = micScale
                            scaleY = micScale
                        }
                        .clip(CircleShape)
                        .background(micBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Ghi nhớ vị trí",
                        tint = micIconColor,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Chạm để ghi nhớ vị trí",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = subTextColor
                )
            }
        }
    }
}
