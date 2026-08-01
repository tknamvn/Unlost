package com.dev.unlost.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    onNavigateToSetPin: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var textToSpeech by remember { mutableStateOf(true) }
    val autoStopMic by viewModel.isAutoStopMicEnabled.collectAsState()
    val autoStopTimeout by viewModel.autoStopTimeoutSeconds.collectAsState()
    val isPinLockEnabled by viewModel.isPinLockEnabled.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshPinLockState()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
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
                        SettingsToggleItem(
                            icon = Icons.Outlined.Mic,
                            title = "Tự động ngắt thu âm",
                            subtitle = if (autoStopMic) "Tự ngắt khi ngưng nói $autoStopTimeout.0 giây" else "Thu âm liên tục tới khi bấm dừng",
                            checked = autoStopMic,
                            onCheckedChange = viewModel::onAutoStopMicToggleChanged
                        )
                        AnimatedVisibility(visible = autoStopMic) {
                            TimeoutSliderSection(
                                timeoutSeconds = autoStopTimeout,
                                onTimeoutChanged = viewModel::onAutoStopTimeoutChanged
                            )
                        }
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
                            onCheckedChange = { enabled ->
                                viewModel.onPinToggleChanged(enabled, onNavigateToSetPin)
                            }
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
private fun TimeoutSliderSection(
    timeoutSeconds: Int,
    onTimeoutChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 38.dp, end = 4.dp, top = 0.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Thời gian ngắt:", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("$timeoutSeconds giây", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
        Slider(
            value = timeoutSeconds.toFloat(),
            onValueChange = { onTimeoutChanged(it.roundToInt()) },
            valueRange = 2f..10f,
            steps = 7,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
private fun SettingsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Cài đặt", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
    }
}
