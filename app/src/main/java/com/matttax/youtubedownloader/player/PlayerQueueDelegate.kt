package com.matttax.youtubedownloader.player

import com.matttax.youtubedownloader.player.model.PlayerMediaMetadata

interface PlayerQueueDelegate {
    fun playShuffled(playlist: List<PlayerMediaMetadata>)
    fun seekInQueue(position: Int)
    fun initQueue(size: Int, shuffle: Boolean)
    fun getQueue(): List<Int>?
    fun shiftItemInQueue(from: Int, to: Int)
}
