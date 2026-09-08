package com.dev.unlost.ui.screens.pin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinKeypad(
    onDigitClick: (String) -> Unit,
    onBackspaceClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .widthIn(max = 320.dp)
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .alpha(if (enabled) 1.0f else 0.4f),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        KeypadRow(digits = listOf("1", "2", "3"), onDigitClick = onDigitClick, enabled = enabled)
        KeypadRow(digits = listOf("4", "5", "6"), onDigitClick = onDigitClick, enabled = enabled)
        KeypadRow(digits = listOf("7", "8", "9"), onDigitClick = onDigitClick, enabled = enabled)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.size(68.dp))

            KeypadButton(onClick = { if (enabled) onDigitClick("0") }, enabled = enabled) {
                Text(
                    text = "0",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            KeypadButton(onClick = { if (enabled) onBackspaceClick() }, enabled = enabled) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Backspace,
                    contentDescription = "Xóa",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun KeypadRow(
    digits: List<String>,
    onDigitClick: (String) -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        digits.forEach { digit ->
            KeypadButton(onClick = { if (enabled) onDigitClick(digit) }, enabled = enabled) {
                Text(
                    text = digit,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    onClick: () -> Unit,
    enabled: Boolean,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(68.dp)
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                ambientColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                spotColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            .clip(CircleShape)
            .background(containerColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
