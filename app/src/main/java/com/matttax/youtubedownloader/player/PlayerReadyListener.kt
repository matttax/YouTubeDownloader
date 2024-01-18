package com.matttax.youtubedownloader.player

import androidx.media3.common.Player

class PlayerReadyListener(
    private val onReady: () -> Unit,
    private val onUnready: () -> Unit = { },
): Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        when(playbackState) {
            Player.STATE_READY -> onReady.invoke()
            Player.STATE_IDLE -> onUnready.invoke()
            else -> {}
        }
    }
}
