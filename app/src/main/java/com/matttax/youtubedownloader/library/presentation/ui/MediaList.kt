package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.core.ui.*
import com.matttax.youtubedownloader.core.ui.UiMediaModel
import com.matttax.youtubedownloader.library.presentation.DialogOnScreen
import com.matttax.youtubedownloader.library.presentation.LibraryViewModel
import com.matttax.youtubedownloader.library.presentation.PlaylistDeletionOptions
import com.matttax.youtubedownloader.library.presentation.ui.medialist.MediaItemCallback
import com.matttax.youtubedownloader.library.presentation.ui.medialist.MediaListView
import com.matttax.youtubedownloader.library.repositories.model.MediaItem
import kotlinx.coroutines.flow.map

@Composable
fun MediaList(
    viewModel: LibraryViewModel,
    modifier: Modifier = Modifier
) {
    val titleText by viewModel.playlistName.collectAsState("All media")
    val mediaList by viewModel.mediaList.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val currentPlayingUri by viewModel.currentPlayingUri.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    var dialogOnScreen by remember { mutableStateOf<DialogOnScreen>(DialogOnScreen.None) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Option(resId = R.drawable.ic_shuffle) {
                viewModel.onPlayShuffled()
            }
            Title(text = titleText)
            Spacer(Modifier.weight(1f))
            if (titleText != "All media") {
                Option(resId = R.drawable.ic_edit) {

                }
                Option(resId = R.drawable.ic_delete) {
                    dialogOnScreen = DialogOnScreen.DeleteCurrentPlaylist
                }
            }
        }
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
                            dialogOnScreen = DialogOnScreen.DeleteMedia(position)
                        }

                        override fun onMoveClick(position: Int) {
                            viewModel.getMediaItemPlaylists(mediaList[position].id)
                            dialogOnScreen = DialogOnScreen.MoveMediaToPlaylist(position)
                        }

                        override fun onEditClick(position: Int) {
                            dialogOnScreen = DialogOnScreen.EditMedia(position)
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

    when (val dialog = dialogOnScreen) {
        is DialogOnScreen.DeleteMedia -> {
            YesNoDialog(
                text = "Are you ready to delete this item?",
                onYes = { viewModel.onDeleteItem(mediaList[dialog.position]) },
                onDismiss = { dialogOnScreen = DialogOnScreen.None }
            )
        }
        is DialogOnScreen.DeleteCurrentPlaylist -> {
            YesNoDialog(
                text = "Are you ready to delete this playlist?",
                onYes = { viewModel.onRemoveCurrentPlaylist() },
                onDismiss = { dialogOnScreen = DialogOnScreen.None }
            ) {
                CheckboxOption(
                    text = "Remove all items",
                    checkedState = viewModel.playlistDeletionOptions
                        .map { it == PlaylistDeletionOptions.PLAYLIST_WITH_ITEMS },
                    isCheckboxTransparent = false,
                    modifier = Modifier.fillMaxWidth(0.9f).padding(15.dp),
                    onCheck = { viewModel.onPlaylistDeletionOptionsChanged(removeItems = it) }
                )
            }
        }
        is DialogOnScreen.MoveMediaToPlaylist -> {
            YesNoDialog(
                text = "Add to playlist",
                onYes = {
                    mediaList[dialog.position].id?.let { id ->
                        viewModel.onAddMediaItemToSelectedPlaylists(id)
                    }
                },
                onDismiss = {
                    dialogOnScreen = DialogOnScreen.None
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
        is DialogOnScreen.EditMedia -> {
            EditDialog(
                mediaItem = mediaList[dialog.position],
                onEdit = { newTitle, newAuthor ->
                    viewModel.onEditItem(dialog.position, newTitle, newAuthor)
                },
                onDismiss = { dialogOnScreen = DialogOnScreen.None }
            )
        }
        is DialogOnScreen.None -> {}
    }
}

fun MediaItem.toUiMediaModel(): UiMediaModel {
    return UiMediaModel(
        id = path,
        thumbnailUri, title, author, durationSeconds
    )
}
