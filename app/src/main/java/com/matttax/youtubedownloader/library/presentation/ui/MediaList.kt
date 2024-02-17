package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.matttax.youtubedownloader.core.ui.*
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.library.presentation.LibraryViewModel
import com.matttax.youtubedownloader.library.presentation.ui.medialist.MediaItemCallback
import com.matttax.youtubedownloader.library.presentation.ui.medialist.MediaListView
import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import kotlinx.coroutines.flow.map

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

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPlaylistDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
    ) {
        Title(text = titleText)
        AndroidView(
            factory = { context ->
                MediaListView(context).also {
                    it.mediaItemCallback = object : MediaItemCallback {
                        override fun onClick(id: String, position: Int) {
                            if (id == currentPlayingUri) {
                                if (isPlaying) viewModel.onPausePlayback() else viewModel.onResumePlayback()
                            } else viewModel.onSetItem(position)
                        }

                        override fun onDeleteClick(position: Int) {
                            showOptionsFor = position
                            showDeleteDialog = true
                        }

                        override fun onMoveClick(position: Int) {
                            showOptionsFor = position
                            viewModel.getMediaItemPlaylists(mediaList[position].id)
                            showPlaylistDialog = true
                        }
                    }
                    it.onDragged = { from, to ->
                        viewModel.onItemsShifted(from, to)
                    }
                    it.init(
                        listFlow = viewModel.mediaList
                            .map { list ->
                                list.map { item -> item.toUiMediaModel() }
                            },
                        eventFlow = viewModel.listEventFlow,
                        currentUriFlow = viewModel.currentPlayingUri,
                        isPlayingFlow = viewModel.isPlaying,
                        scope = scope
                    )
                }
            }
        )
    }
    if (showDeleteDialog) {
        YesNoDialog(
            text = "Are you ready to delete this item?",
            onYes = {
                showOptionsFor?.let {
                    viewModel.onDeleteItem(mediaList[it])
                }
            },
            onDismiss = {
                showDeleteDialog = false
                showOptionsFor = null
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
                showOptionsFor = null
                viewModel.getMediaItemPlaylists(null)
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
