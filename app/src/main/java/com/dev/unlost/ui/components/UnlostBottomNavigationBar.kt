package com.dev.unlost.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class BottomTab {
    HOME, SEARCH, SETTINGS
}

@Composable
fun UnlostBottomNavigationBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == BottomTab.HOME,
            onClick = { onTabSelected(BottomTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Trang chủ"
                )
            },
            label = { Text("Trang chủ") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0E5E53),
                selectedTextColor = Color(0xFF0E5E53),
                indicatorColor = Color(0xFFD1FAE5),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )
        NavigationBarItem(
            selected = currentTab == BottomTab.SEARCH,
            onClick = { onTabSelected(BottomTab.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Tìm kiếm"
                )
            },
            label = { Text("Tìm kiếm") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0E5E53),
                selectedTextColor = Color(0xFF0E5E53),
                indicatorColor = Color(0xFFD1FAE5),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )
        NavigationBarItem(
            selected = currentTab == BottomTab.SETTINGS,
            onClick = { onTabSelected(BottomTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Cài đặt"
                )
            },
            label = { Text("Cài đặt") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0E5E53),
                selectedTextColor = Color(0xFF0E5E53),
                indicatorColor = Color(0xFFD1FAE5),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            )
        )
    }
}
