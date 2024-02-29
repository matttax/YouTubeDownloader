package com.matttax.youtubedownloader.player.providers

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicBoolean

class PlayerReadyProvider(
    private val onStateChanged: (Boolean) -> Unit
): PlayerProvider<Boolean> {

    private val _isPlaying = MutableStateFlow(false)
    override val valueFlow: StateFlow<Boolean>
        get() = _isPlaying.asStateFlow()

    private val isInTransition = AtomicBoolean(false)

    override fun onPlaybackStateChanged(playbackState: Int) {
        when(playbackState) {
            Player.STATE_READY -> onStateChanged(true)
            Player.STATE_IDLE -> onStateChanged(false)
            else -> {}
        }
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        if (reason != Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
            isInTransition.set(true)
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if (!_isPlaying.value) {
            isInTransition.set(false)
        }
        if (!isInTransition.get()) {
            _isPlaying.value = isPlaying
        } else {
            isInTransition.set(false)
        }
    }

    override fun notifyValueChanged(value: Boolean) {
        _isPlaying.value = value
    }
}
