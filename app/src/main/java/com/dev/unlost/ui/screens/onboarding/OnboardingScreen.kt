package com.dev.unlost.ui.screens.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.unlost.R

@Composable
fun OnboardingScreen(
    onStartClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Section: App Logo, Name & Tagline
            HeaderSection()

            // Center Illustration Section: Phone & Floating Cards
            CenterIllustrationSection()

            // Bottom Section: Titles & CTA Button
            BottomSection(onStartClick = onStartClick)
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // App logo container in circular mint badge
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF80F5D2)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_unlost_logo),
                contentDescription = "Unlost Logo",
                modifier = Modifier.size(54.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Name
        Text(
            text = "Unlost",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E5E53)
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Tagline
        Text(
            text = "Đừng bao giờ mất đồ nữa",
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun CenterIllustrationSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // Dashed lines connecting cards to phone
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            val center = Offset(size.width / 2f, size.height / 2f)

            // Top-left connection line
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(size.width * 0.22f, size.height * 0.35f),
                end = Offset(center.x - 50.dp.toPx(), center.y - 30.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                pathEffect = stroke
            )

            // Top-right connection line
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(size.width * 0.78f, size.height * 0.38f),
                end = Offset(center.x + 50.dp.toPx(), center.y - 10.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                pathEffect = stroke
            )

            // Bottom-left connection line
            drawLine(
                color = Color(0xFFCBD5E1),
                start = Offset(size.width * 0.25f, size.height * 0.65f),
                end = Offset(center.x - 50.dp.toPx(), center.y + 40.dp.toPx()),
                strokeWidth = 2.dp.toPx(),
                pathEffect = stroke
            )
        }

        // Floating Card 1: Top-Left (Wallet / Credit Card)
        FloatingCard(
            icon = Icons.Outlined.CreditCard,
            iconTint = Color(0xFFEAB308), // Warm Amber/Yellow
            rotation = -12f,
            modifier = Modifier.offset(x = (-105).dp, y = (-45).dp)
        )

        // Floating Card 2: Top-Right (Book / Notebook)
        FloatingCard(
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            iconTint = Color(0xFF334155), // Dark Slate Navy
            rotation = 12f,
            modifier = Modifier.offset(x = 100.dp, y = (-35).dp)
        )

        // Floating Card 3: Bottom-Left (Key)
        FloatingCard(
            icon = Icons.Outlined.Key,
            iconTint = Color(0xFF0E5E53), // Dark Teal
            rotation = -8f,
            modifier = Modifier.offset(x = (-95).dp, y = 50.dp)
        )

        // Central Phone Frame
        PhoneFrame()
    }
}

@Composable
private fun PhoneFrame() {
    val cornerRadius = 22.dp
    Box(
        modifier = Modifier
            .size(width = 110.dp, height = 180.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
            .background(Color.White, RoundedCornerShape(cornerRadius))
            .border(width = 3.dp, color = Color(0xFF0E5E53), shape = RoundedCornerShape(cornerRadius)),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Lower teal section with microphone icon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp)
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
                contentDescription = "Voice Mic",
                tint = Color(0xFF0E5E53),
                modifier = Modifier.size(28.dp)
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
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.08f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(Color.White, shape)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun BottomSection(
    onStartClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title
        Text(
            text = "Ghi nhớ bằng giọng nói\nTiếng Việt",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A),
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Subtitle
        Text(
            text = "Tìm lại mọi thứ dễ dàng chỉ với một câu hỏi. Hoạt động offline, bảo mật tuyệt đối.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = Color(0xFF64748B),
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Start Button
        Button(
            onClick = onStartClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0E5E53)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Bắt đầu ngay",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen(onStartClick = {})
}
