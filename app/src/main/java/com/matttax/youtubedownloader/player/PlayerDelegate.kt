package com.matttax.youtubedownloader.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.matttax.youtubedownloader.core.model.Format
import com.matttax.youtubedownloader.player.listeners.StreamingMediaProvider
import com.matttax.youtubedownloader.player.listeners.PlayerReadyProvider
import com.matttax.youtubedownloader.player.model.MediaStreamingOptions
import com.matttax.youtubedownloader.player.model.PlayerMediaMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class PlayerDelegate @Inject constructor(
    @ApplicationContext context: Context
) {
    val playingFormat: Format?
        get() = _playingFormat
    val streamingOptions: MediaStreamingOptions?
        get() = _streamingOptions
    val player: Player
        get() = _player!!

    var playlist: List<PlayerMediaMetadata> = emptyList()
        private set

    private var _playingFormat: Format? = null
    private var _streamingOptions: MediaStreamingOptions? = null

    private val _isVideoReady = MutableStateFlow(false)
    val isVideoReady = _isVideoReady.asStateFlow()

    private var _player: Player? = null
    private val streamingMediaProvider = StreamingMediaProvider()
    private val playerReadyProvider = PlayerReadyProvider(
        onReady = { _isVideoReady.value = true },
        onUnready = { _isVideoReady.value = false }
    )

    var shuffled = false
        private set

    init {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                _player = controllerFuture.get().apply {
                    prepare()
                    if (shuffleModeEnabled) shuffled = true
                    streamingMediaProvider.notifyValueChanged(currentMediaItem?.mediaId)
                    playerReadyProvider.notifyValueChanged(isPlaying)
                    addListener(playerReadyProvider)
                    addListener(streamingMediaProvider)
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    @Synchronized
    fun play(
        item: PlayerMediaMetadata,
        startPosition: Int = 0,
        savePosition: Boolean = false,
        format: Format? = null
    ) {
        play(listOf(item), startPosition, savePosition, format)
    }

    @Synchronized
    fun play(
        playlist: List<PlayerMediaMetadata>,
        startPosition: Int = 0,
        savePosition: Boolean = false,
        format: Format? = null
    ) {
        val stablePlayer = _player ?: return
        _playingFormat = format ?: playlist[startPosition].contentUri?.let { Format.Video(url = it) }
        if (shuffled) {
            this.playlist = playlist
            stablePlayer.shuffleModeEnabled = false
        }
        if (playlist == this.playlist) {
            stablePlayer.seekTo(startPosition, C.TIME_UNSET)
            if (!stablePlayer.isPlaying) stablePlayer.play()
            this.playlist = playlist
            return
        }
        this.playlist = playlist
        val currentPos = stablePlayer.currentPosition
        stablePlayer.apply {
            clearMediaItems()
            playlist.forEach { playerMediaMetadata ->
                MediaItem.Builder()
                    .setUri(Uri.parse(playerMediaMetadata.contentUri ?: format?.url))
                    .setMediaId(
                        playerMediaMetadata.contentUri
                            .also { streamingMediaProvider.notifyValueChanged(it) }
                            ?: ""
                    )
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(playerMediaMetadata.author)
                            .setTitle(playerMediaMetadata.title)
                            .setArtworkUri(Uri.parse(playerMediaMetadata.thumbnailUri))
                            .build()
                    ).build().also { addMediaItem(it) }
            }
            seekTo(startPosition, C.TIME_UNSET)
            if (savePosition) {
                seekTo(currentPos)
            }
            play()
            playerReadyProvider.notifyValueChanged(true)
        }
    }

    fun notifyShuffled() {
        shuffled = true
    }

    fun setStreamingOptions(streamingOptions: MediaStreamingOptions) {
        _streamingOptions = streamingOptions
    }

    fun release() {
        _player?.removeListener(streamingMediaProvider)
        _player?.removeListener(playerReadyProvider)
        _player?.release()
    }

    fun pause() {
        _player?.pause()
        playerReadyProvider.notifyValueChanged(false)
        streamingMediaProvider.notifyValueChanged(null)
    }

    fun resume() = _player?.play()

    fun getCurrentPlayingUri() = streamingMediaProvider.valueFlow

    fun getIsPlaying() = playerReadyProvider.valueFlow

    fun clear() = _player?.clearMediaItems()
}
