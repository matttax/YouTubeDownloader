package com.matttax.youtubedownloader.library.presentation.ui.medialist


interface MediaItemCallback {
    fun onClick(id: String, position: Int) {}
    fun onDeleteClick(position: Int) {}
    fun onMoveClick(position: Int) {}
    fun onEditClick(position: Int) {}
}
