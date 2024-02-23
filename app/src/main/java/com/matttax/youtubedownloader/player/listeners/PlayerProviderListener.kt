package com.matttax.youtubedownloader.player.listeners

import androidx.media3.common.Player
import kotlinx.coroutines.flow.StateFlow

interface PlayerProviderListener<T> : Player.Listener {
    val valueFlow: StateFlow<T>
    fun notifyValueChanged(value: T)
}
