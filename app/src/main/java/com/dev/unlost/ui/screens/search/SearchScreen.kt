package com.dev.unlost.ui.screens.search

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun SearchScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAddItem: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val itemToDelete by viewModel.itemToDelete.collectAsState()

    itemToDelete?.let { item ->
        DeleteConfirmDialog(
            item = item,
            onConfirm = { viewModel.confirmDeleteItem() },
            onDismiss = { viewModel.dismissDeleteDialog() }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF8FAFA)) {
        Scaffold(
            bottomBar = {
                UnlostBottomNavigationBar(
                    currentTab = BottomTab.SEARCH,
                    onTabSelected = { tab ->
                        when (tab) {
                            BottomTab.HOME -> onNavigateToHome()
                            BottomTab.SEARCH -> { }
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
                Spacer(modifier = Modifier.height(8.dp))
                SearchInputBar(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    onMicClick = onNavigateToAddItem
                )
                Spacer(modifier = Modifier.height(10.dp))
                FilterChipsRow(
                    categories = viewModel.categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (results.isNotEmpty()) {
                    Text(
                        text = "Tìm thấy ${results.size} kết quả",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(results, key = { _, item -> item.id }) { index, item ->
                            ItemCard(
                                item = item,
                                isTopMatch = index == 0 && (query.isNotEmpty() || selectedCategory != "Tất cả"),
                                onDeleteClick = { viewModel.requestDeleteItem(item) }
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (query.isEmpty() && selectedCategory == "Tất cả") "Nhập từ khóa hoặc chọn thẻ để tìm kiếm" else "Không tìm thấy đồ vật nào phù hợp",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchInputBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onMicClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        placeholder = { Text("Tìm kiếm đồ đạc...", fontSize = 16.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Xóa từ khóa", tint = Color(0xFF64748B))
                    }
                }
                IconButton(onClick = onMicClick) {
                    Icon(Icons.Default.Mic, contentDescription = "Ghi âm", tint = Color(0xFF0E5E53))
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF76F7D4),
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )
}
