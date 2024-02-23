package com.matttax.youtubedownloader.player

import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.matttax.youtubedownloader.player.model.PlayerMediaMetadata

class PlaybackService : MediaSessionService(), PlayerQueueDelegate {

    private var _mediaSession: MediaSession? = null
    private val mediaSession get() = _mediaSession!!

    private var exoPlayer: ExoPlayer? = null
    private var queueManager: QueueManager? = null

    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        exoPlayer = ExoPlayer.Builder(this).build().also {
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
            player.release()
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

    override fun playShuffled(playlist: List<PlayerMediaMetadata>) {
        exoPlayer?.apply {
            clearMediaItems()
            playlist.forEach { playerMediaMetadata ->
                MediaItem.Builder()
                    .setUri(Uri.parse(playerMediaMetadata.contentUri))
                    .setMediaId(playerMediaMetadata.contentUri ?: "")
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setArtist(playerMediaMetadata.author)
                            .setTitle(playerMediaMetadata.title)
                            .setArtworkUri(Uri.parse(playerMediaMetadata.thumbnailUri))
                            .build()
                    ).build().also { addMediaItem(it) }
            }
        }
        val startPosition = queueManager?.initQueue(playlist.size, shuffled = true)
        exoPlayer?.apply {
            seekTo(startPosition ?: 0, C.TIME_UNSET)
            play()
        }
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
}
