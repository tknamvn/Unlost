package com.dev.unlost.ui.screens.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CenterIllustrationSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            val center = Offset(size.width / 2f, size.height / 2f)

            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(size.width * 0.22f, size.height * 0.35f),
                end = Offset(center.x - 50.dp.toPx(), center.y - 30.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                pathEffect = stroke
            )
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(size.width * 0.78f, size.height * 0.38f),
                end = Offset(center.x + 50.dp.toPx(), center.y - 10.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                pathEffect = stroke
            )
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(size.width * 0.25f, size.height * 0.65f),
                end = Offset(center.x - 50.dp.toPx(), center.y + 40.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                pathEffect = stroke
            )
        }

        FloatingCard(
            icon = Icons.Outlined.CreditCard,
            iconTint = Color(0xFFEAB308),
            rotation = -12f,
            modifier = Modifier.offset(x = (-95).dp, y = (-40).dp)
        )

        FloatingCard(
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            iconTint = Color(0xFF334155),
            rotation = 12f,
            modifier = Modifier.offset(x = 90.dp, y = (-30).dp)
        )

        FloatingCard(
            icon = Icons.Outlined.Key,
            iconTint = Color(0xFF0E5E53),
            rotation = -8f,
            modifier = Modifier.offset(x = (-85).dp, y = 45.dp)
        )

        PhoneFrame()
    }
}

@Composable
private fun PhoneFrame() {
    val cornerRadius = 22.dp
    Box(
        modifier = Modifier
            .size(width = 105.dp, height = 170.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(Color.White, RoundedCornerShape(cornerRadius))
            .border(width = 3.dp, color = Color(0xFF0E5E53), shape = RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = cornerRadius - 2.dp,
                        bottomEnd = cornerRadius - 2.dp
                    )
                )
                .background(Color(0xFF76F7D4)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = Color(0xFF0E5E53),
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun FloatingCard(
    icon: ImageVector,
    iconTint: Color,
    rotation: Float,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .rotate(rotation)
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.06f),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(Color.White, shape)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
    }
}
