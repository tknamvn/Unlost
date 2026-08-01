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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
fun SearchScreen(
    onNavigateToAddItem: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val results by viewModel.searchResults.collectAsState()
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
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent
        ) { _ ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = 8.dp)
            ) {
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        itemsIndexed(results, key = { _, item -> item.id }) { index, item ->
                            SwipeableItemCard(
                                item = item,
                                isTopMatch = index == 0 && (query.isNotEmpty() || selectedCategory != "Tất cả"),
                                onEditClick = { viewModel.requestEditItem(item) },
                                onDelete = {
                                    handleSearchItemDelete(item, viewModel, snackbarHostState, scope)
                                }
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (query.isEmpty() && selectedCategory == "Tất cả") "Nhập từ khóa hoặc chọn thẻ để tìm kiếm" else "Không tìm thấy đồ vật nào phù hợp",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

private fun handleSearchItemDelete(
    item: LostItem,
    viewModel: SearchViewModel,
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
            .padding(horizontal = 24.dp),
        placeholder = { Text("Tìm kiếm đồ đạc...", fontSize = 16.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Xóa từ khóa", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                IconButton(onClick = onMicClick) {
                    Icon(Icons.Default.Mic, contentDescription = "Ghi âm", tint = MaterialTheme.colorScheme.primary)
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )
}
