package com.matttax.youtubedownloader.library

import com.matttax.youtubedownloader.library.datasource.entities.MediaItemEntity

data class MediaItem(
    val title: String,
    val author: String,
    val description: String,
    val thumbnailUri: String,
    val hasVideo: Boolean,
    val durationSeconds: Int,
    val path: String
)

fun MediaItemEntity.toMediaItem() = MediaItem(
    title, author, description, thumbnailPath, hasVideo, durationSeconds, path
)
