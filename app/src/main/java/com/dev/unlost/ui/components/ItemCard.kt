package com.dev.unlost.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.ui.theme.WarningYellow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ItemCard(
    item: LostItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTopMatch: Boolean = false
) {
    val cardBg = if (isTopMatch) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
    val cardBorder = if (isTopMatch) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTopMatch) 4.dp else 2.dp),
        border = if (isTopMatch) BorderStroke(1.5.dp, cardBorder) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isTopMatch) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarningYellow)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Khớp nhất",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (icon, bgColor, iconTint) = getItemIconAndColors(item.title)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = item.location, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    if (isTopMatch) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Lưu lúc: ${formatTimeDetail(item.timestamp)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                if (!isTopMatch) {
                    Text(text = formatTimeAgo(item.timestamp), fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun getItemIconAndColors(title: String): Triple<ImageVector, Color, Color> {
    val lower = title.lowercase()
    val primaryColor = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    return when {
        lower.contains("chìa khóa") || lower.contains("khoá") -> Triple(Icons.Outlined.Key, primaryContainer, primaryColor)
        lower.contains("ví") || lower.contains("bóp") || lower.contains("thẻ") -> Triple(Icons.Outlined.CreditCard, primaryContainer, primaryColor)
        lower.contains("sách") || lower.contains("hộ chiếu") -> Triple(Icons.AutoMirrored.Outlined.MenuBook, surfaceVariant, primaryColor)
        else -> Triple(Icons.Outlined.Search, surfaceVariant, onSurfaceVariant)
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)
    return when {
        minutes < 1 -> "Vừa xong"
        minutes < 60 -> "${minutes}p trước"
        hours < 24 -> "${hours}h trước"
        days == 1L -> "Hôm qua"
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
    }
}

private fun formatTimeDetail(timestamp: Long): String {
    val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    return "$time (${formatTimeAgo(timestamp)})"
}
