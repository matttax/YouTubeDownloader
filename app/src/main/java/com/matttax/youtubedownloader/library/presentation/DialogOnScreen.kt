package com.matttax.youtubedownloader.library.presentation

sealed interface DialogOnScreen {
    object None: DialogOnScreen
    object DeleteCurrentPlaylist: DialogOnScreen
    data class EditMedia(val position: Int): DialogOnScreen
    data class DeleteMedia(val position: Int): DialogOnScreen
    data class MoveMediaToPlaylist(val position: Int): DialogOnScreen
}
