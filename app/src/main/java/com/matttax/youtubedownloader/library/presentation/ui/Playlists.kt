package com.matttax.youtubedownloader.library.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matttax.youtubedownloader.R
import com.matttax.youtubedownloader.core.ui.YesNoDialog
import com.matttax.youtubedownloader.core.ui.theme.YouTubeRed
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
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 10.dp
                    )
                    .height(50.dp),
                value = newPlaylistName,
                onValueChange = { newPlaylistName = it },
                placeholder = { Text(text = "Name", fontSize = 12.sp) },
                singleLine = true,

                shape = RoundedCornerShape(20),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = YouTubeRed,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.LightGray.copy(alpha = 0.2f),
                ),
                textStyle = TextStyle.Default.copy(fontSize = 12.sp)
            )
        }
    }
}
