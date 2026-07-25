package com.dev.unlost.util

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.onSwipeBack(
    onBack: () -> Unit,
    minDragDistance: Float = 80f
): Modifier = this.pointerInput(onBack) {
    var totalDragX = 0f
    detectHorizontalDragGestures(
        onDragStart = { totalDragX = 0f },
        onDragEnd = {
            if (totalDragX > minDragDistance) {
                onBack()
            }
        },
        onDragCancel = { totalDragX = 0f },
        onHorizontalDrag = { _, dragAmount ->
            if (dragAmount > 0 || totalDragX > 0) {
                totalDragX += dragAmount
            }
        }
    )
}
