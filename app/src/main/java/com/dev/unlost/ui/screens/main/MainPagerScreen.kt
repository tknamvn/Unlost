package com.dev.unlost.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.dev.unlost.ui.components.BottomTab
import com.dev.unlost.ui.components.UnlostBottomNavigationBar
import com.dev.unlost.ui.screens.home.HomeScreen
import com.dev.unlost.ui.screens.search.SearchScreen
import com.dev.unlost.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun MainPagerScreen(
    onNavigateToAddItem: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )

    val currentTab = when (pagerState.currentPage) {
        0 -> BottomTab.HOME
        1 -> BottomTab.SEARCH
        else -> BottomTab.SETTINGS
    }

    Scaffold(
        bottomBar = {
            UnlostBottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = { tab ->
                    val targetPage = when (tab) {
                        BottomTab.HOME -> 0
                        BottomTab.SEARCH -> 1
                        BottomTab.SETTINGS -> 2
                    }
                    scope.launch {
                        pagerState.animateScrollToPage(targetPage)
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 -> HomeScreen(
                    onNavigateToAddItem = onNavigateToAddItem,
                    onNavigateToSearch = {
                        scope.launch { pagerState.animateScrollToPage(1) }
                    }
                )
                1 -> SearchScreen(
                    onNavigateToAddItem = onNavigateToAddItem
                )
                2 -> SettingsScreen()
            }
        }
    }
}
