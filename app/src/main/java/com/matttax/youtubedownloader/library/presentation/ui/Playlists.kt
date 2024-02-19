package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.core.ui.EditTextField
import com.matttax.youtubedownloader.core.ui.YesNoDialog
import com.matttax.youtubedownloader.library.repositories.model.Playlist
import kotlinx.coroutines.flow.StateFlow

@Composable
fun Playlists(
    modifier: Modifier,
    playlistsState: StateFlow<List<Playlist>>,
    onNewCreate: (String) -> Unit,
    onSelected: (Int) -> Unit
) {
    val playlists by playlistsState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    Column(
        modifier = modifier.padding(top = 2.dp),
    ) {
        Title(text = "Playlists")
        Row(
            modifier = Modifier.padding(5.dp)
        ) {
            PlaylistItem(
                drawableId = R.drawable.ic_add,
                text = "New"
            ) {
                showDialog = true
            }
            LazyRow {
                items(playlists.size) {
                    PlaylistItem(
                        drawableId = R.drawable.ic_playlist,
                        text = playlists[it].name
                    ) {
                        playlists[it].id?.let { id -> onSelected(id) }
                    }
                }
            }
        }
    }
    if (showDialog) {
        YesNoDialog(
            text = "New playlist",
            onYes = { onNewCreate(newPlaylistName) },
            onDismiss = {
                showDialog = false
                newPlaylistName = ""
            },
            yesText = "Create"
        ) {
            EditTextField(
                initialValue = newPlaylistName,
                hint = "Name"
            ) {
                newPlaylistName = it
            }
        }
    }
}
