package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import com.matttax.youtubedownloader.core.ui.CheckboxOption
import com.matttax.youtubedownloader.core.ui.MediaItem
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.core.ui.YesNoDialog
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
    var showOptionsFor by remember { mutableStateOf<Int?>(null) }
    val clearFocus = remember { { if (showOptionsFor != null) showOptionsFor = null } }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                clearFocus()
                return Offset.Zero
            }
        }
    }
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
        LazyColumn(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(mediaList.size) { index ->
                Box(
                    contentAlignment = Alignment.CenterEnd
                ) {
                    MediaItem(
                        videoData = mediaList[index].toUiMediaModel(),
                        onClick = {
                            clearFocus()
                            viewModel.onSetItem(index)
                        },
                        onLongClick = {
                            showOptionsFor = if (index == showOptionsFor)
                                null
                            else index
                        },
                    )
                    PopUpMenu(
                        isPopped = showOptionsFor == index,
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
