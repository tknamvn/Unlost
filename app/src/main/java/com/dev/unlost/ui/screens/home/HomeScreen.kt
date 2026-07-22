package com.dev.unlost.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.unlost.ui.components.ItemCard

@Composable
fun HomeScreen(
    onNavigateToAddItem: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val items by viewModel.itemList.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFA)
    ) {
        Scaffold(
            bottomBar = {
                HomeBottomNavigationBar(
                    onSearchClick = onNavigateToSearch
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HomeVoiceBanner(onBannerClick = onNavigateToAddItem)
                }

                item {
                    RecentItemsHeader()
                }

                if (items.isEmpty()) {
                    item {
                        EmptyItemsPlaceholder()
                    }
                } else {
                    items(items, key = { it.id }) { item ->
                        ItemCard(
                            item = item,
                            onDeleteClick = { viewModel.deleteItem(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeVoiceBanner(onBannerClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onBannerClick),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF76F7D4))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bạn đang để đồ ở đâu?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0E5E53)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0E5E53)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Ghi nhớ vị trí",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Chạm để ghi nhớ vị trí",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0E5E53)
            )
        }
    }
}

@Composable
private fun RecentItemsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mới lưu gần đây",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Text(
            text = "Xem tất cả",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0E5E53),
            modifier = Modifier.clickable { }
        )
    }
}

@Composable
private fun EmptyItemsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Chưa có đồ vật nào được lưu.\nChạm nút mic phía trên để ghi nhớ!",
            fontSize = 15.sp,
            color = Color(0xFF94A3B8),
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun HomeBottomNavigationBar(onSearchClick: () -> Unit) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = "Trang chủ") },
            label = { Text("Trang chủ") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0E5E53),
                selectedTextColor = Color(0xFF0E5E53),
                indicatorColor = Color(0xFFD1FAE5)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onSearchClick,
            icon = { Icon(Icons.Outlined.Search, contentDescription = "Tìm kiếm") },
            label = { Text("Tìm kiếm") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Outlined.Settings, contentDescription = "Cài đặt") },
            label = { Text("Cài đặt") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )
    }
}
