package com.dev.unlost.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun InteractiveSwipeBackContainer(
    onBack: () -> Unit,
    enabled: Boolean = true,
    targetPageContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (!enabled) {
        content()
        return
    }

    val density = LocalDensity.current
    val screenWidthPx = with(density) { LocalConfiguration.current.screenWidthDp.dp.toPx() }
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    val swipeModifier = Modifier.pointerInput(enabled, screenWidthPx) {
        var dragAmountAccumulator = 0f
        detectHorizontalDragGestures(
            onDragStart = {
                dragAmountAccumulator = 0f
            },
            onDragEnd = {
                val shouldComplete = offsetX.value > screenWidthPx * 0.35f
                scope.launch {
                    if (shouldComplete) {
                        offsetX.animateTo(
                            targetValue = screenWidthPx,
                            animationSpec = tween(durationMillis = 150)
                        )
                        onBack()
                    } else {
                        offsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(stiffness = 500f)
                        )
                    }
                }
            },
            onDragCancel = {
                scope.launch {
                    offsetX.animateTo(0f, spring(stiffness = 500f))
                }
            },
            onHorizontalDrag = { change, dragAmount ->
                if (dragAmount > 0 || offsetX.value > 0f) {
                    change.consume()
                    dragAmountAccumulator = (offsetX.value + dragAmount).coerceIn(0f, screenWidthPx)
                    scope.launch {
                        offsetX.snapTo(dragAmountAccumulator)
                    }
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(swipeModifier)
    ) {
        // Parallel Left Page: Target page returning to
        if (targetPageContent != null && offsetX.value > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = -screenWidthPx + offsetX.value
                    }
            ) {
                targetPageContent()
            }
        }

        // Parallel Right Page: Current page standing on
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = offsetX.value
                }
        ) {
            content()
        }
    }
}
