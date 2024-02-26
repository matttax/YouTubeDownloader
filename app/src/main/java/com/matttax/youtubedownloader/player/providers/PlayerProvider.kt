package com.matttax.youtubedownloader.player.providers

import androidx.media3.common.Player
import kotlinx.coroutines.flow.StateFlow

interface PlayerProvider<T> : Player.Listener {
    val valueFlow: StateFlow<T>
    fun notifyValueChanged(value: T)
}
