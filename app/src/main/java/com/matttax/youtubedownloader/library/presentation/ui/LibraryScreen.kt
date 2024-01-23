package com.matttax.youtubedownloader.library.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.core.ui.Player
import com.matttax.youtubedownloader.library.presentation.LibraryViewModel

@Composable
fun LibraryScreen(
    modifier: Modifier,
    viewModel: LibraryViewModel
) {
    val isPlaying by viewModel.isPlaying.collectAsState()
    Column(
        modifier = modifier
    ) {
        if (!isPlaying) {
            Playlists(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.16f)
                    .padding(vertical = 7.dp)
                    .shadow(1.dp),
                onNewClick = {

                }
            )
        }
        MediaList(
            modifier = Modifier.weight(0.7f),
            mediaListFlow = viewModel.mediaList,
            onSelect = viewModel::onSetItem
        )
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
