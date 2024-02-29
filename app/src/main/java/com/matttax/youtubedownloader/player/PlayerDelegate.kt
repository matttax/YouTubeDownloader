package com.matttax.youtubedownloader.player

import androidx.media3.common.Player
import com.matttax.youtubedownloader.core.model.Format
import com.matttax.youtubedownloader.player.model.PlayerMediaMetadata
import kotlinx.coroutines.flow.StateFlow

interface PlayerDelegate {
    fun play(
        item: PlayerMediaMetadata,
        savePosition: Boolean,
        format: Format?
    )
    fun play(
        playlist: List<PlayerMediaMetadata>,
        startPosition: Int,
        shuffled: Boolean,
        savePosition: Boolean,
        format: Format?
    )
    fun pause()
    fun resume()
    fun initQueue(size: Int, shuffle: Boolean)
    fun seekInQueue(position: Int)
    fun shiftItemInQueue(from: Int, to: Int)
    fun getQueue(): List<Int>?
    fun getCurrentPlayingUri(): StateFlow<String?>
    fun getIsPlaying(): StateFlow<Boolean>
    fun getIsReady(): StateFlow<Boolean>
    fun getPlayingFormat(): Format?
    fun getPlayerInstance(): Player?
}
