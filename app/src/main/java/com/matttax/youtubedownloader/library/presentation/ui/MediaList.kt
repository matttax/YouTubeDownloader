package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.matttax.youtubedownloader.core.ui.*
import com.matttax.youtubedownloader.core.ui.PlayingState
import com.matttax.youtubedownloader.core.ui.dragndrop.DragDropColumn
import com.matttax.youtubedownloader.core.ui.utils.UiMediaModel
import com.matttax.youtubedownloader.library.presentation.LibraryViewModel
import com.matttax.youtubedownloader.library.repositories.model.MediaItem

@Composable
fun MediaList(
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    val titleText by viewModel.playlistName.collectAsState()
    val mediaList by viewModel.mediaList.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val currentPlayingUri by viewModel.currentPlayingUri.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    var showOptionsFor by remember { mutableStateOf<Int?>(null) }
    val clearFocus = remember { { if (showOptionsFor != null) showOptionsFor = null } }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .clickable { }
            .pointerInput(Unit) {
                detectTapGestures { clearFocus() }
            }
    ) {
        Title(text = titleText)
        DragDropColumn(
            items = mediaList,
            onSwap = viewModel::onItemsSwapped
        ) { index, item ->
            PlayableMediaItem(
                videoData = mediaList[index].toUiMediaModel(),
                playingState = when {
                    item.path == currentPlayingUri && isPlaying -> PlayingState.PLAYING
                    item.path == currentPlayingUri && !isPlaying -> PlayingState.PAUSED
                    else -> PlayingState.NONE
                },
                onClick = {
                    clearFocus()
                    if (item.path == currentPlayingUri) {
                        if (isPlaying) viewModel.onPausePlayback() else viewModel.onResumePlayback()
                    } else viewModel.onSetItem(index)
                },
                onSwipe = {
                    showOptionsFor = if (index == showOptionsFor)
                        null
                    else index
                },
                swipeEnabled = currentPlayingUri == null && (showOptionsFor == null || showOptionsFor == index)
            ) {
                PopUpMenu(
                    isPopped = it,
                    onDeleteClick = { showDeleteDialog = true },
                    onAddToPlaylistClick = {
                        mediaList[index].id?.let {
                            viewModel.getMediaItemPlaylists(it)
                        }
                        showPlaylistDialog = true
                    }
                )
            }
        }
    }
    if (showDeleteDialog) {
        YesNoDialog(
            text = "Are you ready to delete this item?",
            onYes = {
                showOptionsFor?.let { viewModel.onDeleteItem(mediaList[it].path) }
            },
            onDismiss = {
                showDeleteDialog = false
                clearFocus()
            }
        )
    }
    if (showPlaylistDialog) {
        YesNoDialog(
            text = "Add to playlist",
            onYes = {
                showOptionsFor?.let {
                    mediaList[it].id?.let {
                        id -> viewModel.onAddMediaItemToSelectedPlaylists(id)
                    }
                }
            },
            onDismiss = {
                showPlaylistDialog = false
                viewModel.onDeselectPlaylists()
                clearFocus()
            }
        ) {
            LazyColumn {
                items(playlists.size) { index ->
                    playlists[index].id?.let {
                        CheckboxOption(
                            text = playlists[index].name,
                            checkedState = viewModel.getPlaylistSelectionState(it),
                            onCheck = { state -> viewModel.onSelectPlaylist(it, state) }
                        )
                    }
                }
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
