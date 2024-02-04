package com.matttax.youtubedownloader.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import com.matttax.youtubedownloader.core.model.Format
import com.matttax.youtubedownloader.player.model.MediaStreamingOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _isVideoReady = MutableStateFlow(false)
    val isVideoReady = _isVideoReady.asStateFlow()

    private val mediaItemListener = MediaItemListener()
    private val playerReadyListener = PlayerReadyListener(
        onReady = { _isVideoReady.value = true },
        onUnready = { _isVideoReady.value = false }
    )

    init {
        exoPlayer.prepare()
        exoPlayer.addListener(playerReadyListener)
        exoPlayer.addListener(mediaItemListener)
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

    fun play(playlist: List<String>, startPosition: Int = 0, queue: List<Int>? = null) {
        playlist.forEach {
            val item = MediaItem.Builder().setUri(it).setMediaId(it).build()
            exoPlayer.addMediaItem(item)
        }
        exoPlayer.seekTo(startPosition, C.TIME_UNSET)
        queue?.let { setQueue(it) }
        exoPlayer.play()
        _playing = Format.Video(url = playlist[startPosition])
    }

    fun setStreamingOptions(streamingOptions: MediaStreamingOptions) {
        _streamingOptions = streamingOptions
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    fun setQueue(queue: List<Int>) {
        exoPlayer.setShuffleOrder(
            ShuffleOrder.DefaultShuffleOrder(queue.toIntArray(), 0)
        )
        exoPlayer.shuffleModeEnabled = true
    }

    fun release() = exoPlayer.release()

    fun clear() = exoPlayer.clearMediaItems()

    fun pause() = exoPlayer.pause()

    fun resume() = exoPlayer.play()

    fun getCurrentPlayingUri() = mediaItemListener.currentPlayingUri

    fun getIsPlaying() = playerReadyListener.isPlaying
}
