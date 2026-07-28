package com.dev.unlost.ui.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.dev.unlost.R

@Composable
fun IdleView() {
    Column(modifier = GlanceModifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Text("SẴN SÀNG GHI NHỚ", style = TextStyle(color = ColorProvider(Color(0xFFFFC107)), fontSize = 11.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text("Chạm Mic & nói vị trí đồ đạc...", style = TextStyle(color = ColorProvider(Color.White), fontSize = 13.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text("VD: \"Ví da ở trên bàn học\"", style = TextStyle(color = ColorProvider(Color(0xFF8E9DAE)), fontSize = 11.sp))
    }
}

@Composable
fun RecordingView(liveText: String) {
    Column(modifier = GlanceModifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Text("🔴 ĐANG NHẬN DIỆN...", style = TextStyle(color = ColorProvider(Color(0xFFFF4081)), fontSize = 11.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text(
            text = if (liveText.isNotEmpty()) "\"$liveText\"" else "\"Tôi để chìa khóa ở vách tủ...\"",
            style = TextStyle(color = ColorProvider(Color(0xFFFFD54F)), fontSize = 13.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        )
        Spacer(modifier = GlanceModifier.height(3.dp))
        WaveformBars(liveText = liveText)
    }
}

@Composable
private fun WaveformBars(liveText: String) {
    val barColors = listOf(Color(0xFFEC407A), Color(0xFFAED581), Color(0xFF4DB6AC), Color(0xFFAB47BC), Color(0xFFD4E157))
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 0..4) {
            Box(modifier = GlanceModifier.width(6.dp).height(computeWaveformHeight(i, liveText)).cornerRadius(3.dp).background(ColorProvider(barColors[i]))) {}
            if (i < 4) Spacer(modifier = GlanceModifier.width(4.dp))
        }
    }
}

private fun computeWaveformHeight(index: Int, liveText: String): Dp {
    val base = listOf(10, 16, 24, 16, 11)
    if (liveText.isEmpty()) return base[index].dp
    val hash = (liveText.hashCode() + index * 11) and 0x7FFFFFFF
    return (base[index] + (hash % 9) - 4).coerceIn(6, 26).dp
}

@Composable
fun ParsedView(title: String, location: String) {
    Column(modifier = GlanceModifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Text("${getCategoryIcon(title)} ${title.ifEmpty { "Đồ đạc mới" }}", style = TextStyle(color = ColorProvider(Color.White), fontSize = 13.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = GlanceModifier.height(1.dp))
        Text("📍 ${location.ifEmpty { "Chưa xác định vị trí" }}", style = TextStyle(color = ColorProvider(Color(0xFF80CBC4)), fontSize = 11.sp))
        Spacer(modifier = GlanceModifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = GlanceModifier.background(ImageProvider(R.drawable.bg_widget_btn_save)).padding(horizontal = 16.dp, vertical = 5.dp).clickable(actionRunCallback<SaveItemAction>()),
                contentAlignment = Alignment.Center
            ) {
                Text("✓ Lưu vị trí", style = TextStyle(color = ColorProvider(Color.White), fontSize = 11.sp, fontWeight = FontWeight.Bold))
            }
            Spacer(modifier = GlanceModifier.width(6.dp))
            Box(
                modifier = GlanceModifier.background(ImageProvider(R.drawable.bg_widget_btn_cancel)).padding(horizontal = 12.dp, vertical = 5.dp).clickable(actionRunCallback<CancelItemAction>()),
                contentAlignment = Alignment.Center
            ) {
                Text("✕ Hủy", style = TextStyle(color = ColorProvider(Color(0xFFFF8A80)), fontSize = 11.sp, fontWeight = FontWeight.Bold))
            }
        }
    }
}

private fun getCategoryIcon(title: String): String {
    val lower = title.lowercase()
    return when {
        lower.contains("chìa khóa") || lower.contains("khoá") || lower.contains("key") -> "🔑"
        lower.contains("ví") || lower.contains("thẻ") || lower.contains("tiền") -> "💳"
        lower.contains("kính") -> "👓"
        lower.contains("điện thoại") || lower.contains("phone") -> "📱"
        lower.contains("giấy") || lower.contains("hồ sơ") -> "📄"
        lower.contains("balo") || lower.contains("túi") -> "🎒"
        else -> "🔑"
    }
}

@Composable
fun SavedView() {
    Column(modifier = GlanceModifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Text("✓ Đã lưu vị trí thành công!", style = TextStyle(color = ColorProvider(Color(0xFF66BB6A)), fontSize = 13.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text("Đã đồng bộ vào sổ Unlost.", style = TextStyle(color = ColorProvider(Color(0xFF8E9DAE)), fontSize = 11.sp))
    }
}

@Composable
fun ErrorView(errorMsg: String) {
    Column(modifier = GlanceModifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Text("⚠️ " + (if (errorMsg.isNotEmpty()) errorMsg else "Không thể nhận diện"), style = TextStyle(color = ColorProvider(Color(0xFFFF5252)), fontSize = 13.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = GlanceModifier.height(2.dp))
        Text("Chạm mic để thử lại", style = TextStyle(color = ColorProvider(Color(0xFF8E9DAE)), fontSize = 11.sp))
    }
}
