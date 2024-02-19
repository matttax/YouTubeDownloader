package com.matttax.youtubedownloader.library.presentation

sealed interface DialogOnScreen {
    object None: DialogOnScreen
    data class Edit(val position: Int): DialogOnScreen
    data class Delete(val position: Int): DialogOnScreen
    data class Move(val position: Int): DialogOnScreen
}
