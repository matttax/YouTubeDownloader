package com.matttax.youtubedownloader.player

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ShuffleOrder
import com.matttax.youtubedownloader.core.shift
import java.util.concurrent.atomic.AtomicReference

class QueueManager(
    private val exoPlayer: ExoPlayer
) {

    val queue: List<Int>?
        get() = _queue.get()

    private var _queue = AtomicReference<List<Int>?>(null)

    fun initQueue(size: Int, shuffled: Boolean = false) {
        exoPlayer.shuffleModeEnabled = true
        val queueList = (0 until size).toList()
        _queue.set(
            if (shuffled) queueList.shuffled() else queueList
        )
        trySetQueue()
    }

    fun onItemMoved(from: Int, to: Int) {
        val queueValue = _queue.get()?.toMutableList() ?: return
        queueValue.shift(from, to)
        _queue.set(queueValue)
        trySetQueue()
    }

    @OptIn(UnstableApi::class)
    private fun trySetQueue() {
        val queueArray = _queue.get()?.toIntArray() ?: return
        if (exoPlayer.mediaItemCount != queueArray.size) return
        exoPlayer.setShuffleOrder(
            ShuffleOrder.DefaultShuffleOrder(queueArray, 0)
        )
    }
}
