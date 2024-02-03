package com.matttax.youtubedownloader.player

import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerReadyListener(
    private val onReady: () -> Unit,
    private val onUnready: () -> Unit = { },
): Player.Listener {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    override fun onPlaybackStateChanged(playbackState: Int) {
        when(playbackState) {
            Player.STATE_READY -> onReady.invoke()
            Player.STATE_IDLE -> onUnready.invoke()
            else -> {}
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }
}
