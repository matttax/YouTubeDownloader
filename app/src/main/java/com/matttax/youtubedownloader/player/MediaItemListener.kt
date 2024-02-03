package com.matttax.youtubedownloader.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MediaItemListener: Player.Listener {

    private val _currentPlayingUri = MutableStateFlow<String?>(null)
    val currentPlayingUri = _currentPlayingUri.asStateFlow()

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        _currentPlayingUri.value = mediaItem?.mediaId
    }
}
