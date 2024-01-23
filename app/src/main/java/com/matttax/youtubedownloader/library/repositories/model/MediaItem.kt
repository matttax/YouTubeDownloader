package com.matttax.youtubedownloader.library.repositories.model


data class MediaItem(
    val title: String,
    val author: String,
    val description: String,
    val thumbnailUri: String,
    val hasVideo: Boolean,
    val durationSeconds: Int,
    val path: String
)
