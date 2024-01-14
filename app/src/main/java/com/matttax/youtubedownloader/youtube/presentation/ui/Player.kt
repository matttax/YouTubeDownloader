package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.flow.Flow

@Composable
fun Player(videoReady: Flow<Boolean>, exoPlayer: ExoPlayer) {
    val showPlayer by videoReady.collectAsState(initial = false)
    if (showPlayer) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            factory = { context ->
                PlayerView(context).also {
                    it.player = exoPlayer
                }
            }
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
