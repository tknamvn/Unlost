package com.dev.unlost.ui.screens.pin

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
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinRecoverySheet(
    onDismiss: () -> Unit,
    onPinResetSuccess: (newPin: String, phone: String) -> Unit
) {
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: Phone, 2: OTP, 3: New PIN
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Khôi phục mã PIN bằng SĐT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (step) {
                1 -> {
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Số điện thoại khôi phục") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                2 -> {
                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = { otpInput = it },
                        label = { Text("Mã xác thực OTP (Mô phỏng: 1234)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                3 -> {
                    OutlinedTextField(
                        value = newPinInput,
                        onValueChange = { if (it.length <= 4) newPinInput = it },
                        label = { Text("Nhập mã PIN 4 số mới") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMsg!!, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text("Hủy", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        when (step) {
                            1 -> {
                                if (phoneInput.length >= 9) step = 2 else errorMsg = "Vui lòng nhập SĐT hợp lệ"
                            }
                            2 -> {
                                if (otpInput.isNotEmpty()) step = 3 else errorMsg = "Vui lòng nhập mã OTP"
                            }
                            3 -> {
                                if (newPinInput.length == 4) {
                                    onPinResetSuccess(newPinInput, phoneInput)
                                } else {
                                    errorMsg = "Mã PIN phải gồm đúng 4 chữ số"
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (step == 3) "Lưu mã PIN mới" else "Tiếp tục", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
