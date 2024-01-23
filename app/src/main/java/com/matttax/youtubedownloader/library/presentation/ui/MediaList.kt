package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.ui.MediaItem
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MediaList(
    modifier: Modifier,
    mediaListFlow: StateFlow<List<MediaItem>>,
    onSelect: (Int) -> Unit
) {
    val list by mediaListFlow.collectAsState()
    Column (
        modifier = modifier
    ) {
        Title(text = "All media")
        LazyColumn(
            modifier = Modifier.padding(5.dp)
        ) {
            items(list.size) { index ->
                MediaItem(
                    videoData = list[index].toUiMediaModel(),
                    onClick = { onSelect(index) },
                    onLongClick = { }
                )
            }
        }
    }
}

fun MediaItem.toUiMediaModel(): UiMediaModel {
    return UiMediaModel(
        id = path,
        thumbnailUri, title, author, durationSeconds
    )
}
