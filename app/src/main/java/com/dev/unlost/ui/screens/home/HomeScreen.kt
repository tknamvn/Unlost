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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dev.unlost.domain.model.LostItem
import com.dev.unlost.ui.components.EditItemBottomSheet
import com.dev.unlost.ui.components.SwipeableItemCard
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onNavigateToAddItem: () -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val items by viewModel.itemList.collectAsState()
    val itemToEdit by viewModel.itemToEdit.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    itemToEdit?.let { item ->
        EditItemBottomSheet(
            item = item,
            onSave = viewModel::saveEditedItem,
            onDismiss = viewModel::dismissEditSheet
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8FAFA)
    ) {
        Scaffold(
            topBar = { HomeTopBar() },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
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
                            SwipeableItemCard(
                                item = item,
                                onEditClick = { viewModel.requestEditItem(item) },
                                onDelete = {
                                    handleItemDelete(item, viewModel, snackbarHostState, scope)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun handleItemDelete(
    item: LostItem,
    viewModel: HomeViewModel,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope
) {
    viewModel.deleteItem(item)
    scope.launch {
        val result = snackbarHostState.showSnackbar(
            message = "Đã xóa \"${item.title}\"",
            actionLabel = "Hoàn tác",
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) {
            viewModel.undoDeleteItem(item)
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
