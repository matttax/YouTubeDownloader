package com.matttax.youtubedownloader.player

import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.matttax.youtubedownloader.core.model.Format
import com.matttax.youtubedownloader.player.model.PlayerMediaMetadata
import com.matttax.youtubedownloader.player.providers.PlayerReadyProvider
import com.matttax.youtubedownloader.player.providers.StreamingMediaProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlaybackService : MediaSessionService(), PlayerDelegate {

    private var _mediaSession: MediaSession? = null
    private val mediaSession
        get() = _mediaSession!!

    private var exoPlayer: ExoPlayer? = null
    private var playingFormat: Format? = null
    private var queueManager: QueueManager? = null

    private val isVideoReady = MutableStateFlow(false)

    private val streamingMediaProvider = StreamingMediaProvider()
    private val playerReadyProvider = PlayerReadyProvider(
        onStateChanged = { isVideoReady.value = it }
    )

    private var playlist: List<PlayerMediaMetadata> = emptyList()

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            addListener(streamingMediaProvider)
            addListener(playerReadyProvider)
        }.also {
            _mediaSession = MediaSession.Builder(this, it).build()
            queueManager = QueueManager(it)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession.player
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return _mediaSession
    }

    override fun onDestroy() {
        _mediaSession?.run {
            player.apply {
                removeListener(playerReadyProvider)
                removeListener(streamingMediaProvider)
                release()
            }
            release()
            _mediaSession = null
        }
        super.onDestroy()
    }

    inner class LocalBinder: Binder() {
        fun getService(): PlaybackService = this@PlaybackService
    }

    override fun onBind(intent: Intent?): IBinder? {
        return if (intent?.categories?.contains(Intent.CATEGORY_MONKEY) == true) {
            binder
        } else super.onBind(intent)
    }

    override fun play(item: PlayerMediaMetadata, savePosition: Boolean, format: Format?) {
        play(
            playlist = listOf(item),
            startPosition = 0,
            shuffled = false,
            savePosition = savePosition,
            format = format
        )
    }

    override fun play(
        playlist: List<PlayerMediaMetadata>,
        startPosition: Int,
        shuffled: Boolean,
        savePosition: Boolean,
        format: Format?
    ) {
        val stablePlayer = exoPlayer ?: return
        playingFormat = format ?: playlist[startPosition].contentUri?.let { Format.Video(url = it) }
        if (this.playlist == playlist && !shuffled) {
            stablePlayer.seekTo(queueManager?.queue?.get(startPosition) ?: startPosition, C.TIME_UNSET)
            if (!stablePlayer.isPlaying){
                stablePlayer.play()
            }
            return
        }
        val currentPos = stablePlayer.currentPosition
        stablePlayer.apply {
            clearMediaItems()
            playlist.forEach { playerMediaMetadata ->
                MediaItem.Builder()
                    .setUri(Uri.parse(playerMediaMetadata.contentUri ?: format?.url))
                    .setMediaId(playerMediaMetadata.contentUri ?: "")
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(playerMediaMetadata.author)
                            .setTitle(playerMediaMetadata.title)
                            .setArtworkUri(Uri.parse(playerMediaMetadata.thumbnailUri))
                            .build()
                    ).build().also { addMediaItem(it) }
            }
            val firstInQueue = queueManager?.initQueue(playlist.size, shuffled)
            if (shuffled) {
                seekTo(firstInQueue ?: startPosition, C.TIME_UNSET)
            } else {
                seekTo(startPosition, C.TIME_UNSET)
            }
            if (savePosition) {
                seekTo(currentPos)
            }
            prepare()
            play()
        }
        this.playlist = queueManager?.queue?.let { queue ->
            List(playlist.size) { playlist[queue[it]] }
        } ?: playlist
    }

    override fun seekInQueue(position: Int) {
        exoPlayer?.seekTo(queueManager?.queue?.get(position) ?: position, C.TIME_UNSET)
        if (exoPlayer?.isPlaying == false) exoPlayer?.play()
    }

    override fun initQueue(size: Int, shuffle: Boolean) {
        val startPosition = queueManager?.initQueue(size, shuffle) ?: 0
        if (exoPlayer?.isPlaying == false) {
            exoPlayer?.seekTo(
                startPosition, C.TIME_UNSET
            )
        }
    }

    override fun getQueue(): List<Int>? {
        return queueManager?.queue
    }

    override fun shiftItemInQueue(from: Int, to: Int) {
        queueManager?.onItemMoved(from, to)
    }

    override fun getPlayerInstance(): Player? {
        return exoPlayer
    }

    override fun pause() {
        streamingMediaProvider.notifyValueChanged(null)
        playerReadyProvider.notifyValueChanged(false)
        exoPlayer?.pause()
    }

    override fun resume() {
        exoPlayer?.play()
    }

    override fun getCurrentPlayingUri() = streamingMediaProvider.valueFlow

    override fun getIsPlaying() = playerReadyProvider.valueFlow

    override fun getIsReady() = isVideoReady.asStateFlow()

    override fun getPlayingFormat() = playingFormat
}
