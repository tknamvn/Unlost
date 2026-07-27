package com.dev.unlost.ui.screens.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.dev.unlost.ui.components.BottomTab
import com.dev.unlost.ui.components.UnlostBottomNavigationBar
import com.dev.unlost.ui.screens.home.HomeScreen
import com.dev.unlost.ui.screens.search.SearchScreen
import com.dev.unlost.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun MainPagerScreen(
    onNavigateToAddItem: () -> Unit,
    onNavigateToSetPin: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.HOME) }
    var directTargetPage by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            if (directTargetPage == null) {
                selectedTab = when (page) {
                    0 -> BottomTab.HOME
                    1 -> BottomTab.SEARCH
                    else -> BottomTab.SETTINGS
                }
            }
        }
    }

    val onTabSelected: (BottomTab) -> Unit = { tab ->
        selectedTab = tab
        val target = when (tab) {
            BottomTab.HOME -> 0
            BottomTab.SEARCH -> 1
            BottomTab.SETTINGS -> 2
        }
        val current = pagerState.currentPage
        if (target != current) {
            scope.launch {
                if (abs(target - current) > 1) {
                    directTargetPage = target
                    pagerState.animateScrollToPage(
                        page = 1,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                    pagerState.scrollToPage(target)
                    directTargetPage = null
                } else {
                    pagerState.animateScrollToPage(
                        page = target,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            UnlostBottomNavigationBar(
                currentTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 2,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            userScrollEnabled = true
        ) { page ->
            val activePage = if (page == 1 && directTargetPage != null) {
                directTargetPage!!
            } else {
                page
            }

            when (activePage) {
                0 -> HomeScreen(
                    onNavigateToAddItem = onNavigateToAddItem,
                    onNavigateToSearch = { onTabSelected(BottomTab.SEARCH) }
                )
                1 -> SearchScreen(
                    onNavigateToAddItem = onNavigateToAddItem
                )
                2 -> SettingsScreen(
                    onNavigateToSetPin = onNavigateToSetPin
                )
            }
        }
    }
}
