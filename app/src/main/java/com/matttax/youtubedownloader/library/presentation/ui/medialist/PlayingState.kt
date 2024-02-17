package com.matttax.youtubedownloader.library.presentation.ui.medialist

sealed class PlayingState {
    object None: PlayingState()
    data class Paused(val uri: String): PlayingState()
    data class Playing(val uri: String): PlayingState()
}
