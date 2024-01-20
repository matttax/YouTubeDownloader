package com.matttax.youtubedownloader.youtube.presentation.states

data class DownloadState(
    val isDownloading: Boolean = false,
    val progress: Float? = null,
    val isCompleted: Boolean = false,
)
