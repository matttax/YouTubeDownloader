package com.matttax.youtubedownloader.library.presentation.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
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
    val isMediaSelected by viewModel.isMediaItemSelected.collectAsState()
    val name by viewModel.playlistName.collectAsState()

    Column(
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = !isMediaSelected,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = LinearOutSlowInEasing
                )
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Playlists(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.16f)
                    .padding(vertical = 7.dp)
                    .shadow(1.dp),
                playlistsState = viewModel.playlists,
                onNewCreate = viewModel::onAddPlaylist,
                onSelected = viewModel::onChoosePlaylist
            )
        }
        MediaList(
            modifier = Modifier.weight(0.7f),
            viewModel = viewModel
        )
        AnimatedVisibility(
            visible = isMediaSelected,
            enter = slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = LinearOutSlowInEasing
                )
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(
                    durationMillis = 600,
                    easing = FastOutSlowInEasing
                )
            )
        ) {
            Player(
                modifier = Modifier
                    .weight(0.3f)
                    .padding(bottom = 15.dp),
                exoPlayer = viewModel.getExoInstance(),
                showNavigationButtons = true
            )
        }
    }

    BackHandler(enabled = isMediaSelected || name != "All media") {
        if (isMediaSelected) {
            viewModel.onStopPlayback()
        } else if (name != "All media") {
            viewModel.loadAllMedia()
        }
    }
}
