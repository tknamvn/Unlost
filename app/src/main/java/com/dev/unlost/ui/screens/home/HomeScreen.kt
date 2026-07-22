package com.dev.unlost.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.unlost.ui.components.BottomTab
import com.dev.unlost.ui.components.DeleteConfirmDialog
import com.dev.unlost.ui.components.ItemCard
import com.dev.unlost.ui.components.UnlostBottomNavigationBar

@Composable
fun HomeScreen(
    onNavigateToAddItem: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val items by viewModel.itemList.collectAsState()
    val itemToDelete by viewModel.itemToDelete.collectAsState()

    itemToDelete?.let { item ->
        DeleteConfirmDialog(
            item = item,
            onConfirm = { viewModel.confirmDeleteItem() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFA)
    ) {
        Scaffold(
            topBar = { HomeTopBar() },
            bottomBar = {
                UnlostBottomNavigationBar(
                    currentTab = BottomTab.HOME,
                    onTabSelected = { tab ->
                        when (tab) {
                            BottomTab.HOME -> { }
                            BottomTab.SEARCH -> onNavigateToSearch()
                            BottomTab.SETTINGS -> onNavigateToSettings()
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Fixed Pinned Section: Banner + Recent Items Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 4.dp)
                ) {
                    HomeVoiceBanner(onBannerClick = onNavigateToAddItem)

                    Spacer(modifier = Modifier.height(14.dp))

                    RecentItemsHeader(onViewAllClick = onNavigateToSearch)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scrollable Item Cards List (ONLY item cards scroll)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (items.isEmpty()) {
                        item { EmptyItemsPlaceholder() }
                    } else {
                        items(items, key = { it.id }) { item ->
                            ItemCard(
                                item = item,
                                onDeleteClick = { viewModel.requestDeleteItem(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentItemsHeader(onViewAllClick: () -> Unit) {
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
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0E5E53),
            modifier = Modifier.clickable(onClick = onViewAllClick)
        )
    }
}

@Composable
private fun EmptyItemsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Chưa có đồ vật nào được lưu.\nChạm nút mic phía trên để ghi nhớ!",
            fontSize = 14.sp,
            color = Color(0xFF94A3B8)
        )
    }
}
