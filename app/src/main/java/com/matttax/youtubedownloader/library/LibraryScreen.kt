package com.matttax.youtubedownloader.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.ui.MediaItem
import com.matttax.youtubedownloader.core.ui.Player
import com.matttax.youtubedownloader.core.ui.UiMediaModel

@Composable
fun LibraryScreen(
    modifier: Modifier,
    viewModel: LibraryViewModel
) {
    val list by viewModel.mediaList.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    Column(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.weight(0.7f)
        ) {
            items(list.size) { index ->
                MediaItem(
                    videoData = list[index].toUiMediaModel()
                ) {
                    viewModel.onSetItem(index)
                }
            }
        }
        if (isPlaying) {
            Player(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(bottom = 15.dp),
                exoPlayer = viewModel.getExoInstance(),
                showNavigationButtons = true
            )
        }
    }

    BackHandler(enabled = true) {
        viewModel.onStopPlayback()
    }
}

fun MediaItem.toUiMediaModel(): UiMediaModel {
    return UiMediaModel(
        id = path,
        thumbnailUri, title, author, durationSeconds
    )
}
