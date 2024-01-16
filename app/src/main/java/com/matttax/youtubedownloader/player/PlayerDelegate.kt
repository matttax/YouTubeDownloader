package com.matttax.youtubedownloader.player

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.matttax.youtubedownloader.core.model.Format
import com.matttax.youtubedownloader.youtube.download.model.MediaStreamingOptions
import javax.inject.Inject

class PlayerDelegate @Inject constructor(
    val exoPlayer: ExoPlayer
) {
    val playing: Format?
        get() = _playing
    val streamingOptions: MediaStreamingOptions?
        get() = _streamingOptions

    private var _playing: Format? = null
    private var _streamingOptions: MediaStreamingOptions? = null

    init {
        exoPlayer.prepare()
    }

    fun play(format: Format, savePosition: Boolean = false) {
        val currentPos = exoPlayer.currentPosition
        exoPlayer.apply {
            setMediaItem(MediaItem.fromUri(format.url))
            if (savePosition) {
                seekTo(currentPos)
            }
            play()
        }
        _playing = format
    }

    fun setStreamingOptions(streamingOptions: MediaStreamingOptions) {
        _streamingOptions = streamingOptions
    }

    fun release() = exoPlayer.release()

    fun clear() = exoPlayer.clearMediaItems()
}
