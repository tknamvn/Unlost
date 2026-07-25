package com.dev.unlost.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var autoStopMic by remember { mutableStateOf(true) }
    var textToSpeech by remember { mutableStateOf(true) }
    val isPinLockEnabled by viewModel.isPinLockEnabled.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFA)
    ) {
        Scaffold(
            topBar = { SettingsTopBar() },
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SettingsSection(title = "GIỌNG NÓI & NHẬN DIỆN") {
                        SettingsItem(icon = Icons.Outlined.Language, title = "Gói ngôn ngữ Offline", subtitle = "Tiếng Việt (Đã tải - 45MB)")
                        SettingsToggleItem(icon = Icons.Outlined.Mic, title = "Tự động ngắt thu âm", subtitle = "Khi ngưng nói 3.0 giây", checked = autoStopMic, onCheckedChange = { autoStopMic = it })
                        SettingsToggleItem(icon = Icons.AutoMirrored.Outlined.VolumeUp, title = "Đọc kết quả vị trí", subtitle = "Phát giọng nói Text-to-Speech", checked = textToSpeech, onCheckedChange = { textToSpeech = it })
                    }
                }
                item {
                    SettingsSection(title = "BẢO MẬT & DỮ LIỆU") {
                        SettingsToggleItem(
                            icon = Icons.Outlined.Fingerprint,
                            title = "Khóa bảo mật PIN",
                            subtitle = "Yêu cầu mã PIN khi mở ứng dụng",
                            checked = isPinLockEnabled,
                            onCheckedChange = viewModel::togglePinLock
                        )
                        SettingsItem(icon = Icons.Outlined.Lock, title = "Sao lưu mã hóa", subtitle = "Xuất/Nhập dữ liệu cá nhân")
                    }
                }
                item {
                    SettingsSection(title = "LỐI TẮT & TÍCH HỢP") {
                        SettingsItem(icon = Icons.Outlined.Widgets, title = "Quick Settings & Widget", subtitle = "Đặt nút thu âm nhanh")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Cài đặt", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFD1FAE5))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("✓ Offline", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0E5E53))
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
        Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) { content() }
        }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, subtitle: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF0E5E53))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            Text(subtitle, fontSize = 14.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
private fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF0E5E53))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
            Text(subtitle, fontSize = 14.sp, color = Color(0xFF64748B))
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF0E5E53))
        )
    }
}
