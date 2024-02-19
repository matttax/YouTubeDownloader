package com.matttax.youtubedownloader.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
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
    private val queueManager = QueueManager(exoPlayer)

    init {
        exoPlayer.prepare()
        exoPlayer.addListener(playerReadyListener)
        exoPlayer.addListener(mediaItemListener)
    }

    @Synchronized
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

    @Synchronized
    fun play(playlist: List<String>, startPosition: Int = 0, shuffled: Boolean = false) {
        exoPlayer.clearMediaItems()
        exoPlayer.shuffleModeEnabled = false
        playlist.forEach {
            val item = MediaItem.Builder().setUri(it).setMediaId(it).build()
            exoPlayer.addMediaItem(item)
        }
        queueManager.initQueue(playlist.size, shuffled)
        if (shuffled) {
            exoPlayer.seekTo(
                queueManager.queue?.first() ?: startPosition, C.TIME_UNSET
            )
        }
        exoPlayer.play()
        _playing = Format.Video(url = playlist[startPosition])
    }

    fun getQueue(): List<Int>? {
        return queueManager.queue
    }

    fun setStreamingOptions(streamingOptions: MediaStreamingOptions) {
        _streamingOptions = streamingOptions
    }

    fun shiftItemInQueue(from: Int, to: Int) {
        queueManager.onItemMoved(from, to)
    }

    fun release() = exoPlayer.release()

    fun clear() = exoPlayer.clearMediaItems()

    fun pause() = exoPlayer.pause()

    fun resume() = exoPlayer.play()

    fun getCurrentPlayingUri() = mediaItemListener.currentPlayingUri

    fun getIsPlaying() = playerReadyListener.isPlaying
}
