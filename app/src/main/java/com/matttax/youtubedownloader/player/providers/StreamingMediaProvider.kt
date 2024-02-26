package com.matttax.youtubedownloader.player.providers

import androidx.media3.common.MediaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StreamingMediaProvider : PlayerProvider<String?> {

    private val _currentPlayingUri = MutableStateFlow<String?>(null)
    override val valueFlow: StateFlow<String?>
        get() = _currentPlayingUri.asStateFlow()

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        _currentPlayingUri.value = mediaItem?.mediaId
    }

    override fun notifyValueChanged(value: String?) {
        _currentPlayingUri.value = value
    }
}
