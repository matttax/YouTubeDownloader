package com.matttax.youtubedownloader.youtube.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun Player(exoPlayer: ExoPlayer) {
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
}
