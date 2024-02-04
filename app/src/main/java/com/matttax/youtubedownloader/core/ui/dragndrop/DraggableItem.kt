package com.matttax.youtubedownloader.core.ui.dragndrop

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex

@ExperimentalFoundationApi
@Composable
fun LazyItemScope.DraggableItem(
    dragDropState: DragDropState,
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(isDragging: Boolean) -> Unit
) {
    val current: Float by animateFloatAsState(dragDropState.draggingItemOffset)
    val previous: Float by animateFloatAsState(dragDropState.previousItemOffset.value)
    val dragging = index == dragDropState.currentIndexOfDraggedItem

    val draggingModifier = if (dragging) {
        Modifier
            .zIndex(1f)
            .graphicsLayer { translationY = current }
    } else if (index == dragDropState.previousIndexOfDraggedItem) {
        Modifier
            .zIndex(1f)
            .graphicsLayer { translationY = previous }
    } else {
        Modifier.animateItemPlacement(
            tween(easing = LinearEasing)
        )
    }
    Column(modifier = modifier.then(draggingModifier)) {
        content(dragging)
    }
}
